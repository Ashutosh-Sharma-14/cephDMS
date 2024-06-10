package com.example.demoDMS1.ServiceImpl;
import com.amazonaws.services.s3.transfer.Upload;
import com.example.demoDMS1.Model.UploadMetadataDTO;
import com.example.demoDMS1.Service.CephService;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.example.demoDMS1.Utility.CephMetadataUtils.createHeadObjectRequest;
import static com.example.demoDMS1.Utility.CephMetadataUtils.printMetadata;
import static org.springframework.http.client.observation.ClientHttpObservationDocumentation.LowCardinalityKeyNames.STATUS;

@Component
@ConfigurationProperties(prefix = "ceph")
@EnableAsync
public class CephServiceImpl implements CephService {

//    @Autowired
//    private CephConfig cephConfig;

//    this is private final to make it immutable and thread safe
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
//    root folder, should not be modified
    private final String bucketName = "test";

    static int counter = 0;

    public CephServiceImpl(){

        AwsBasicCredentials credentials;
        credentials = AwsBasicCredentials.create("4C3B7FJNW8SL9P0EHZPH","Gw5B49P04iCOStQtw8Fx04VNaIB5CbCLEuMBh2GM");

        try
        {
            S3Client client;
            S3Presigner presigner;
            client = S3Client.builder()
                    .endpointOverride(new URI("http://172.17.91.15:7480"))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
//                the below line uses lambda expression
                    .serviceConfiguration(S3Configuration.Builder::pathStyleAccessEnabled)
                    .region(Region.US_EAST_2)
                    .build();

            presigner = S3Presigner.builder()
                    .region(Region.US_EAST_2)
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();

            this.s3Presigner = presigner;
            this.s3Client = client;
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String enableVersioning(String bucketName) {
        GetBucketVersioningRequest getBucketVersioningRequest = GetBucketVersioningRequest.builder()
                .bucket(bucketName)
                .build();

        BucketVersioningStatus currentStatus = s3Client.getBucketVersioning(getBucketVersioningRequest).status();

        if (!BucketVersioningStatus.ENABLED.equals(currentStatus)) {
            PutBucketVersioningRequest putBucketVersioningRequest = PutBucketVersioningRequest.builder()
                    .bucket(bucketName)
                    .versioningConfiguration(conf -> conf.status(BucketVersioningStatus.ENABLED))
                    .build();

            s3Client.putBucketVersioning(putBucketVersioningRequest);
            System.out.println("Bucket versioning enabled for bucket: " + bucketName);
        } else {
            System.out.println("Bucket versioning is already enabled for bucket: " + bucketName);
        }
        return "Versioning enabled for " + bucketName;
    }

    @Override
    public List<String> listVersionOfObject(String bucketName, String objectKey) {
        ListObjectVersionsRequest request = ListObjectVersionsRequest.builder()
                .bucket(bucketName)
                .prefix(objectKey)
                .delimiter("/")
                .build();


        ListObjectVersionsResponse response = s3Client.listObjectVersions(request);
        List<ObjectVersion> res = response.versions();
        List<DeleteMarkerEntry> deleteMarkers = response.deleteMarkers();
        for(ObjectVersion object : res) {
            System.out.println("versionID " + object.versionId());
            System.out.println("eTag " + object.eTag());
            System.out.println("Key/Name " + object.key());
            System.out.println("is latest " + object.isLatest());
            System.out.println("lastModified " + object.lastModified());
            System.out.println("owner " + object.owner());
            System.out.println("restoreStatus " + object.restoreStatus());
            System.out.println("size " + object.size());
            System.out.println("hashCode " + object.hashCode());
            System.out.println();
        }

        System.out.println("Delete Markers:");
        for (DeleteMarkerEntry deleteMarker : deleteMarkers) {
            if (deleteMarker.key().equals(objectKey)) {
                System.out.println("Delete Marker found for object: " + objectKey);
                System.out.println("Version ID: " + deleteMarker.versionId());
                System.out.println("Is Latest: " + deleteMarker.isLatest());
                System.out.println("Last Modified: " + deleteMarker.lastModified());
                System.out.println("Owner: " + deleteMarker.owner());
                System.out.println();
            }
        }
        return List.of();
    }

    @Override
    public ResponseEntity<?> listBuckets() {
        ListBucketsResponse response = s3Client.listBuckets(ListBucketsRequest.builder().build());
        List<Bucket> buckets = response.buckets();

        System.out.println("Listing buckets:");
        for(Bucket bucket: buckets){
            System.out.println(bucket);
        }
        if(buckets.isEmpty()){
            return ResponseEntity.ok().body("No buckets present");
        }
        else{
            return ResponseEntity.ok().body(buckets);
        }
    }

    @Override
    public ResponseEntity<List<String>> listObjects(String bucketName, String prefix) {
        List<String> objectKeys = new ArrayList<>();

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        for (S3Object summary : listResponse.contents()) {
            objectKeys.add(summary.key());
        }
//        System.out.println(objectKeys);

        // Recursively list objects in subdirectories
        for (S3Object summary : listResponse.contents()) {
            System.out.println("this is the key: " + summary.key());
            if (summary.key().endsWith("/")) {
                String subPrefix = summary.key().substring(0, summary.key().length() - 1);
                System.out.println("Subprefix: " + subPrefix);
//                Recursively calling the method to print the keys
                ResponseEntity<List<String>> subObjectsResponse = listObjects(bucketName, subPrefix);
                System.out.println(subObjectsResponse.getBody().stream().toList());

                List<String> subObjects = subObjectsResponse.getBody().stream().toList();
                objectKeys.addAll(subObjects);
            }
        }

        return ResponseEntity.ok().body(objectKeys);
    }

    @Override
    public ResponseEntity<Map<String, Object>> listPaginatedObjects(String bucketName, String prefix, int maxKeys, String continuationToken) {
        if(continuationToken == null){
            Map<String, Object> result = new HashMap<>();
            result.put("Bad Request","No more content to fetch");
            return ResponseEntity.ok().body(result);
        }
        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .maxKeys(maxKeys);

        if(!continuationToken.isEmpty()){
            requestBuilder.continuationToken(continuationToken);
        }

        ListObjectsV2Request request = requestBuilder.build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);
        List<String> objectKeys = response.contents().stream()
                .map(S3Object::key)
//        S3Object-> S3Object.key() == S3Object::key
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        String nextContinuationToken = response.nextContinuationToken();
        result.put("objectKeys", objectKeys);
        result.put("nextContinuationToken", nextContinuationToken);

        System.out.println(nextContinuationToken);
        return ResponseEntity.ok(result);
    }


    @Override
    public void listMetadata(String objectKey) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);

        if (headObjectResponse != null) {
            // Retrieve and print object metadata
            System.out.println("Object Metadata:");
            headObjectResponse.metadata().forEach((key, value) -> System.out.println(key + ": " + value));
            System.out.println("System-Generated Metadata:");
            System.out.println("Content-Type: " + headObjectResponse.contentType());
            System.out.println("Content-Length: " + headObjectResponse.contentLength());
            System.out.println("Last-Modified: " + headObjectResponse.lastModified());
            System.out.println("Version id: " + headObjectResponse.versionId());
            System.out.println("ETag: " + headObjectResponse.eTag());
//            System.out.println("Delete marker: " + headObjectResponse.deleteMarker());
        } else {
            System.out.println("Failed to retrieve object metadata.");
        }
    }

    @Override
    public Map<String, String> addMetadata(UploadMetadataDTO metadataDTO, String bucketName, String objectKey) {
        Map<String, String> updatedMetadata = null;
        try {
            Map<String, String> existingMetadata = null;
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            existingMetadata = headObjectResponse.metadata();

//            merging existing metadata
            updatedMetadata = new HashMap<>();
            if (existingMetadata != null) {
                updatedMetadata.putAll(existingMetadata);
            }

            updatedMetadata.put("Metadata1", metadataDTO.getMetadata1());
            updatedMetadata.put("Metadata2", metadataDTO.getMetadata2());
            updatedMetadata.put("Metadata3", metadataDTO.getMetadata3());

        } catch (NoSuchKeyException e) {
            System.out.println("No key with name: " + objectKey + " present in the specified bucket: " + bucketName);
            Throwable cause = e.getCause();
            if (cause != null) {
                System.out.println("Reason for exception: " + cause);
            }
        }
        return updatedMetadata;
    }

    @Override
    public void listTags(String objectKey){

        GetObjectTaggingRequest getObjectTaggingRequest = GetObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try {
            GetObjectTaggingResponse response = s3Client.getObjectTagging(getObjectTaggingRequest);

            // Add the tags associated with the object to the list
            List<Tag> objectTags = new ArrayList<>(response.tagSet());
//            print all the tags
            System.out.println("Tags for object " + objectKey + ":");
            objectTags.forEach(tag -> System.out.println(tag.key() + ": " + tag.value()));
        } catch (S3Exception e) {
            System.err.println("Error: " + e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public String addTagToObject(String objectKey, String tagKey, String tagValue) {

        GetObjectTaggingRequest getObjectTaggingRequest = GetObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        GetObjectTaggingResponse response = s3Client.getObjectTagging(getObjectTaggingRequest);
        List<Tag> existingTags = new ArrayList<>(response.tagSet());

        Tag newTag = Tag.builder()
                .key(tagKey)
                .value(tagValue)
                .build();
        existingTags.add(newTag);

        PutObjectTaggingRequest request = PutObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .tagging(Tagging.builder().tagSet(existingTags).build())
                .build();

        s3Client.putObjectTagging(request);
        return "Tag- " + tagKey + ": " + tagValue + "added to tagset of " + objectKey;
    }

    @Override
    public void deleteObject(String bucketName, String objectKey) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteRequest);
            System.out.println("Current version of object '" + objectKey + "' deleted successfully.");
        } catch (S3Exception e) {
            System.err.println("Error deleting object '" + objectKey + "': " + e.getMessage());
        }
    }

    @Override
    public String uploadFile(MultipartFile file,
                             String fileYear,
                             String bankName,
                             String accountNo) {
        String key = fileYear + "/" + bankName + "/" + accountNo + "/" + file.getOriginalFilename();

        HeadObjectRequest headObjectRequest = createHeadObjectRequest(bucketName,key);
        HeadObjectResponse headObjectResponse = null;
        try{
//            storing the result of headObjectRequest as response (which is metadata)
            headObjectResponse = s3Client.headObject(headObjectRequest);
            System.out.println("Existing metadata: ");
            printMetadata(headObjectResponse);
        }//        This exception class is provided by S3
        catch (Exception e){
            System.out.println("Checking exception " + e);
        }

        Map<String, String> newMetadata = headObjectResponse != null ? new HashMap<>(headObjectResponse.metadata()) : new HashMap<>();
        newMetadata.put("author-age", "23");
        newMetadata.put("author-name","aman");

        try{
            ByteBuffer input = ByteBuffer.wrap(file.getBytes());
            s3Client.putObject(
                    req -> req.bucket(bucketName).key(key).metadata(newMetadata),
                    RequestBody.fromByteBuffer(input)
            );
        }
        catch (IOException e){
            System.out.println("Error uploading file" + Arrays.toString(e.getStackTrace()));
        }

        // Retrieve and log updated metadata
        headObjectResponse = s3Client.headObject(headObjectRequest);
        System.out.println("Updated metadata:");
        printMetadata(headObjectResponse);

        return "file uploaded successfully to ceph bucket:" + bucketName + "/" + key + "/n";
    }

    @Async
    public CompletableFuture<String> uploadFileAsync(MultipartFile file, String objectKey,UploadMetadataDTO metadataDTO){
        LocalDateTime startTime = LocalDateTime.now();
        Map<String,String> existingMetadata = addMetadata(metadataDTO,bucketName,objectKey);
        try {
            ByteBuffer input = ByteBuffer.wrap(file.getBytes());
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .metadata(existingMetadata)
                            .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromByteBuffer(input)
            );

            LocalDateTime endTime = LocalDateTime.now();
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            URL presignedUrl = presignedRequest.url();

            return CompletableFuture.completedFuture("Start time: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", End time: " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", File uploaded successfully to Ceph bucket: " + bucketName + "/" + objectKey +
                    ", Presigned URL: " + presignedUrl.toString());
        } catch (IOException e) {
            e.getMessage();
            return CompletableFuture.completedFuture("Error uploading file: " + file.getOriginalFilename());
        }
    }

    @Override
    public List<String> uploadMultipleFiles(MultipartFile[] files, String prefix, UploadMetadataDTO metadataDTO) throws ExecutionException, InterruptedException {
        List<CompletableFuture<String>> fileUploadFutures = new ArrayList<>();

        for (MultipartFile file : files) {
            fileUploadFutures.add(uploadFileAsync(file, prefix,metadataDTO));
        }

        CompletableFuture<Void> allOfFileUploadFutures = CompletableFuture.allOf(fileUploadFutures.toArray(new CompletableFuture[0]));
        // waiting for the execution of all fileUploads
        allOfFileUploadFutures.get(); // .get() method throws CheckedException - InterruptedException and ExecutionException

        List<String> fileUploadResults = new ArrayList<>();

        for (CompletableFuture<String> uploadFuture : fileUploadFutures) {
            try {
                String result = uploadFuture.get();
                fileUploadResults.add(result);
            } catch (Exception e) {
                fileUploadResults.add(e.getMessage());
            }
        }
        return fileUploadResults;
    }

//    this method does not work
//    @Override
//    public List<String> uploadFilesToMultiplePrefixes(AccountFilesRequest accountFilesRequest) throws ExecutionException, InterruptedException, IOException {
//        List<CompletableFuture<String>> fileUploadFutures = new ArrayList<>();
////        String bucketName = accountFilesRequest.getBucketName();
//        String fileYear = accountFilesRequest.getFileYear();
//        String bankName = accountFilesRequest.getBankName();
//
////        Here .getAccountFiles() provide a list of list of files. Each fileList is associated with an accountNo
//        for(AccountFiles accountFiles: accountFilesRequest.getAccountFiles()){
//            String accountNo = accountFiles.getAccountNo();
////            .getFiles() method is used to access each file from the list of files associated with one account
//            for(MultipartFile file: accountFiles.getFiles()){
//                fileUploadFutures.add(uploadFileAsync(file, fileYear, bankName, accountNo));
//            }
//        }
//
//        CompletableFuture<Void> allOfFileUploadFutures = CompletableFuture.allOf(fileUploadFutures.toArray(new CompletableFuture[0]));
//        // waiting for the execution of all fileUploads
//        allOfFileUploadFutures.get(); // .get() method throws CheckedException - InterruptedException and ExecutionException
//
//        List<String> fileUploadResults = new ArrayList<>();
//
//        for (CompletableFuture<String> uploadFuture : fileUploadFutures) {
//            try {
//                String result = uploadFuture.get();
//                fileUploadResults.add(result);
//            } catch (Exception e) {
//                fileUploadResults.add(e.getMessage());
//            }
//        }
//
//        return fileUploadResults;
//    }

    @Override
    public String downloadFile(String prefix, String versionId) {
//        String key = fileYear + "/" + bankName + "/" + accountNo + "/" + fileName;
        String downloadPath = System.getProperty("user.home") + "/Downloads";
//        System.out.println(fileName);

        File downloadDir = new File(System.getProperty("user.home") + "/Downloads");
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        try{
//            check if buffering option is available for downloading large files
            s3Client.getObject(
//                    lambda expression. Curly brackets can be removed in case of single statement
                    req -> req.bucket(bucketName).key(prefix).versionId(versionId),
                    Paths.get(downloadPath)
            );
        }
        catch (Exception e){
            e.getStackTrace();
        }
        File downloadedFile = new File(downloadPath);
        if(downloadedFile.length() == 0){
            System.out.println("checking condition");
            return "file not downloaded";
        }

        return "file successfully downloaded at: home/Desktop/" + prefix;
    }

    @Async
    public CompletableFuture<String> downloadFileAsync(String key, String downloadDir){
        LocalDateTime startTime = LocalDateTime.now();
        try{
            File directory = new File(downloadDir);
            if(!directory.exists()){
                directory.mkdirs();
            }

//            creates a placeholder for the file where the bytes read would be saved
            String filename = Paths.get(key).getFileName().toString();
            File file = new File(directory, filename);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            // Get the input stream from the response
            try (InputStream objectInputStream = s3Client.getObject(getObjectRequest);
                 OutputStream outputStream = new FileOutputStream(file)) {

                // Stream the content from the input stream to the output stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = objectInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            LocalDateTime endTime = LocalDateTime.now();
            return CompletableFuture.completedFuture("Start time: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", End time: " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", File downloaded successfully: " + file.getAbsolutePath());

        }
        catch (Exception e){
            return CompletableFuture.completedFuture("Error downloading file: " + e);
        }
    }

    @Override
    public List<String> downloadMultipleFiles(String prefix) throws ExecutionException, InterruptedException {
        String downloadDir = System.getProperty("user.home") + "/Downloads" + "/" + prefix;

//        list of array to store download results received using CompletableFuture
        List<String> downloadResults = new ArrayList<>();
        List<CompletableFuture<String>> fileDownloadFutures = new ArrayList<>();

//        list all objects under the specified prefix
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        for (S3Object object : listResponse.contents()) {
            String key = object.key();
//            size of key is > 1 and size of prefix is 0
            if(object.size() > 0){
                System.out.println(key);
                fileDownloadFutures.add(downloadFileAsync(key, downloadDir));
            }
            else{
                System.out.println(key);
                downloadMultipleFiles(key);
            }
        }

        CompletableFuture<Void> allDownloadFutures = CompletableFuture.allOf(fileDownloadFutures.toArray(new CompletableFuture[0]));
        allDownloadFutures.get(); // Wait for all downloads to complete

        for (CompletableFuture<String> downloadFuture : fileDownloadFutures) {
            downloadResults.add(downloadFuture.get());
        }

        return downloadResults;
    }
}


