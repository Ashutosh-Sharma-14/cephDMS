package com.example.demoDMS1.ServiceImpl;
import com.example.demoDMS1.Entity.MetadataEntity;
import com.example.demoDMS1.Model.CommonResponseDTO;
import com.example.demoDMS1.Model.DownloadRequestDTO;
import com.example.demoDMS1.Model.ListPaginatedObjectsResponse;
import com.example.demoDMS1.Model.UploadRequestDTO;
import com.example.demoDMS1.Repository.MetadataRepository;
import com.example.demoDMS1.Service.CephService;

import com.example.demoDMS1.Service.MetadataService;
import com.example.demoDMS1.Service.UserRoleService;
import com.example.demoDMS1.Utility.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

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

@Component
@ConfigurationProperties(prefix = "ceph")
@EnableAsync
public class CephServiceImpl implements CephService {

//    @Autowired
//    private CephConfig cephConfig;

    @Autowired
    MetadataService metadataService;
    @Autowired
    MetadataRepository metadataRepository;
    @Autowired
    UserRoleService userRoleService;

//    this is private final to make it immutable and thread safe
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
//    root folder, should not be modified
    private final String bucketName = "test";

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
    public ResponseEntity<?> createBucket(String bucketName){
        try{
            CreateBucketRequest createBucketRequest = CreateBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

            CreateBucketResponse createBucketResponse = s3Client.createBucket(createBucketRequest);
        }
        catch (S3Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Unable to create Bucket",HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("Successfully created bucket " + bucketName, HttpStatus.OK);
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
    public ResponseEntity<List<String>> listVersionOfObject(String bucketName, String objectKey) {
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
        return ResponseEntity.ok().body(List.of());
    }

    @Override
    public ResponseEntity<?> listBuckets() {
        ListBucketsResponse response = s3Client.listBuckets(ListBucketsRequest.builder().build());
        List<Bucket> buckets = response.buckets();
        List<String> bucketNames = new ArrayList<>();

        System.out.println("Listing buckets:");
        for( Bucket bucket: buckets){
            bucketNames.add(bucket.name());
            System.out.println(bucket);
        }

        if(buckets.isEmpty()){
            return ResponseEntity.ok().body("No buckets present");
        }
        else{
            return ResponseEntity.ok().body(bucketNames);
        }
    }

    @Override
    public ResponseEntity<List<String>> listObjectsByAuthority(String bucketName, String userRole){
        String userAuthority = userRoleService.getUserAuthorityLevel(userRole);
        List<String> objectKeys = new ArrayList<>();
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        for(S3Object summary: listResponse.contents()){
            String objectKey = summary.key();
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            String authority =  headObjectResponse.metadata().get("userauthoritylevel");
            if (Integer.parseInt(userAuthority) <= Integer.parseInt(authority)) {
                objectKeys.add(objectKey);
            }
        }
        return ResponseEntity.ok().body(objectKeys);
    }

    @Override
    public ResponseEntity<List<String>> listObjects(String bucketName, String prefix) {
        List<String> objectKeys = new ArrayList<>();

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
//                .delimiter("/")
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        System.out.println(listResponse);

        for (S3Object summary : listResponse.contents()) {
//            System.out.println(summary.size());
            String extractedUuid = summary.key().substring(summary.key().lastIndexOf("/")+1);
//            System.out.println("Extracted uuid from original object key: " + extractedUuid);
            String key = metadataRepository.findObjectKeyByUUID(extractedUuid);
            Map<String,String> metadata = metadataRepository.findMetadataByUUID(extractedUuid);
            objectKeys.add(key);
        }

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
    public ResponseEntity<ListPaginatedObjectsResponse> listPaginatedObjects(String bucketName, String prefix, int maxKeys, String continuationToken) {
        ListPaginatedObjectsResponse result = new ListPaginatedObjectsResponse();
        if(continuationToken == null){
            return new ResponseEntity<>(null,HttpStatus.NO_CONTENT);
        }

        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .maxKeys(maxKeys);

//        if the continuation token is empty
        if(!continuationToken.isEmpty()){
            requestBuilder.continuationToken(continuationToken);
        }

        ListObjectsV2Request request = requestBuilder.build();
        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        List<String> objectKeys = response.contents().stream()
                .map(S3Object::key)
//        S3Object-> S3Object.key() == S3Object::key
                .toList();

        List<String> lastModifiedTime = response.contents().stream()
                .map(S3Object -> S3Object.lastModified().toString())
                .toList();

        List<Long> fileSizes = response.contents().stream()
                .map(S3Object::size)
                .toList();

        List<Map<String,String>> metadata = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        for(String key: objectKeys){
            String extractedUuid = key.substring(key.lastIndexOf("/")+1);
            keys.add(metadataRepository.findObjectKeyByUUID(extractedUuid));
            metadata.add(metadataRepository.findMetadataByUUID(extractedUuid));
        }

        String nextContinuationToken = response.nextContinuationToken();
        result.setObjectKeys(keys);
        result.setMetadata(metadata);
        result.setFileSizes(fileSizes);
        result.setLastModifiedTime(lastModifiedTime);
        result.setContinuationToken(nextContinuationToken);

        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<Map<String,String>> listMetadata(String objectKey) {
        Map<String,String> metadata = new HashMap<>();
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);

        if (headObjectResponse != null) {
            // Retrieve and print object metadata
            System.out.println("Object Metadata:");
            headObjectResponse.metadata().forEach((key, value) -> {
                System.out.println(key + ": " + value);
                metadata.put(key,value);
            });
        } else {
            System.out.println("Failed to retrieve object metadata.");
        }
        return ResponseEntity.ok().body(metadata);
    }

    @Override
    public ResponseEntity<Map<String, String>> listSystemGeneratedMetadata(String objectKey) {
        System.out.println("System-Generated Metadata:");
        Map<String,String> metadata = new HashMap<>();
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
//        make sure to add proper exception handling here.
        assert headObjectResponse != null;
        System.out.println("Content-Type: " + headObjectResponse.contentType());
        metadata.put("content-type",headObjectResponse.contentType());

        System.out.println("Content-Length: " + headObjectResponse.contentLength());
        metadata.put("content-length", String.valueOf(headObjectResponse.contentLength()));

        System.out.println("Last-Modified: " + headObjectResponse.lastModified());
        metadata.put("last-modified", String.valueOf(headObjectResponse.lastModified()));

        System.out.println("Version id: " + headObjectResponse.versionId());
        metadata.put("version-id",headObjectResponse.versionId());

        System.out.println("ETag: " + headObjectResponse.eTag());
        metadata.put("e-tag",headObjectResponse.eTag());

        return ResponseEntity.ok().body(metadata);
    }

    @Override
    public Map<String, String> addMetadata(Map<String, String> metadata, String bucketName, String objectKey) {
        return null;
    }

    @Override
    public Map<String, String> addToExistingMetadata(Map<String, String> metadata, String bucketName, String objectKey) {
        Map<String, String> updatedMetadata = null;
        try {
            Map<String, String> existingMetadata;
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            existingMetadata = headObjectResponse.metadata();

//            merging existing metadata
            updatedMetadata = new HashMap<>();
            if (existingMetadata != null) {
                System.out.println("originally metadata exists");
                updatedMetadata.putAll(existingMetadata);
            }

            Set<String> metadataKeys = metadata.keySet();
            for(String metadataKey: metadataKeys){
                updatedMetadata.put(metadataKey,metadata.get(metadataKey));
            }

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
    public ResponseEntity<List<String>> listObjectByAuthorityTags(String bucketName, String userRole) {
        String userAuthority = userRoleService.getUserAuthorityLevel(userRole);
        List<String> objectKeys = new ArrayList<>();

        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        for (S3Object summary : listResponse.contents()) {
            String objectKey = summary.key();
            GetObjectTaggingRequest getObjectTaggingRequest = GetObjectTaggingRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            GetObjectTaggingResponse getObjectTaggingResponse = s3Client.getObjectTagging(getObjectTaggingRequest);
            Map<String, String> tags = getObjectTaggingResponse.tagSet().stream()
                    .collect(Collectors.toMap(Tag::key, Tag::value));

            String authority = tags.get("authorityLevel");
            if (authority != null && Integer.parseInt(userAuthority) <= Integer.parseInt(authority)) {
                objectKeys.add(objectKey);
            }
        }
        return ResponseEntity.ok().body(objectKeys);
    }

    @Override
    public String modifyObjectTag(String objectKey, String tagKey, String tagValue) {

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
    public ResponseEntity<String> addTagToObject(String bucketName,String objectKey,String tagKey, String tagValue){
        return ResponseEntity.ok().body("Tag- " + tagKey + ": " + tagValue + "added to tagset of " + objectKey);
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
                             String bucketName,
                             String objectKey,
                             String userRole,
                             Map<String,String> metadata) {
//        Map<String,String> fileMetadata = addMetadata(metadata,bucketName,objectKey);
//        addTagToObject(bucketName,objectKey,"authorityLevel",userRoleService.getUserAuthorityLevel(userRole));
        Tag tag = Tag.builder().key("authorityLevel").value(userRoleService.getUserAuthorityLevel(userRole)).build();
        try{
            ByteBuffer input = ByteBuffer.wrap(file.getBytes());
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(metadata)
                    .tagging(Tagging.builder().tagSet(Arrays.asList(tag)).build())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromByteBuffer(input)
            );
        }
        catch (IOException e){
            System.out.println("Error uploading file" + Arrays.toString(e.getStackTrace()));
        }

        return "file uploaded successfully to ceph bucket:" + bucketName + "/" + objectKey + "/n";
    }

    public URL createUrl(String bucketName,String objectKey){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url();
    }

    @Async
    public CompletableFuture<String> uploadFileAsync(MultipartFile file,String bucketName,String key, Map<String,String> metadata,String userRole){
        LocalDateTime startTime = LocalDateTime.now();

        Map<String,String> fileMetadata = addToExistingMetadata(metadata,bucketName,key);
        Tag tag = Tag.builder().key("authorityLevel").value(userRoleService.getUserAuthorityLevel(userRole)).build();
        try {
            ByteBuffer input = ByteBuffer.wrap(file.getBytes());
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .metadata(fileMetadata)
                            .tagging(Tagging.builder().tagSet(Arrays.asList(tag)).build())
                            .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromByteBuffer(input)
            );

            LocalDateTime endTime = LocalDateTime.now();
            URL presignedUrl = createUrl(bucketName,key);

            return CompletableFuture.completedFuture("Start time: " + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", End time: " + endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    ", File uploaded successfully to Ceph bucket: " + bucketName + "/" + key +
                    ", Presigned URL: " + presignedUrl.toString());
        } catch (IOException e) {
            e.getMessage();
            return CompletableFuture.completedFuture("Error uploading file: " + file.getOriginalFilename());
        }
    }

    @Override
    public ResponseEntity<CommonResponseDTO<?>> uploadMultipleFiles(UploadRequestDTO uploadRequestDTO) throws ExecutionException, InterruptedException {
        List<CompletableFuture<String>> fileUploadFutures = new ArrayList<>();

        MultipartFile[] files = uploadRequestDTO.getMultipartFiles();
        String bucketName = uploadRequestDTO.getBucketName();
        String userRole = uploadRequestDTO.getUserRole();
        List<Map<String, String>> metadataList = uploadRequestDTO.getMetadata();
//        System.out.println(files.length);
//        System.out.println(bucketName);
//        System.out.println(userRole);
//        System.out.println(metadataList.get(0));

        for (int i = 0; i < files.length; i++) {
            Map<String,String> metadata = metadataList.get(i);
            String objectKey = uploadRequestDTO.getObjectKey() + files[i].getOriginalFilename();
            UUID uuid = UUID.randomUUID();
            String key = uploadRequestDTO.getObjectKey() + uuid;


//            saving the required data in mongodb collection to implement search
            MetadataEntity metadataEntity = new MetadataEntity();
            metadataEntity.setUuid(uuid.toString());
            metadataEntity.setObjectKey(objectKey);
            metadataEntity.setMetadata(metadata);
            metadataRepository.save(metadataEntity);

            fileUploadFutures.add(uploadFileAsync(files[i],bucketName, key,metadata,userRole));
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
        CommonResponseDTO<?> commonResponseDTO = ResponseBuilder.buildUploadResponse(200,"file uploaded successfully",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),fileUploadResults);
        return ResponseEntity.ok().body(commonResponseDTO);
    }

    @Override
    public ResponseEntity<CommonResponseDTO<?>> downloadFile(DownloadRequestDTO downloadRequestDTO) {
        String downloadDestination = System.getProperty("user.home") + "/Downloads/";
        System.out.println(downloadRequestDTO.getVersionId());
        System.out.println(downloadRequestDTO.getObjectKey());

        File downloadDir = new File(downloadDestination);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        try{
//            check if buffering option is available for downloading large files
//            Answer: The buffering is internally managed by S3Client, but if customization is required we need to use get the inputStream and buffer manually
            s3Client.getObject(
//                    lambda expression. Curly brackets can be removed in case of single statement
                    req -> req.bucket(downloadRequestDTO.getBucketName())
                            .key(downloadRequestDTO.getObjectKey())
                            .versionId(downloadRequestDTO.getVersionId()),
                    Paths.get(downloadDestination+downloadRequestDTO.getObjectKey())
            );
        }
        catch (Exception e){
            e.getStackTrace();
        }
        File downloadedFile = new File(downloadDestination+downloadRequestDTO.getObjectKey());
        if(downloadedFile.length() == 0){
            System.out.println("Checking if download is successful");
            CommonResponseDTO<?> commonResponseDTO = ResponseBuilder.unsuccessfulDownloadResponse(501,"No file with ObjectKey: " + downloadRequestDTO.getObjectKey() + " exists");
            return new ResponseEntity<>(commonResponseDTO, HttpStatus.valueOf(501));
        }
        CommonResponseDTO<?> commonResponseDTO = ResponseBuilder.successfulDownloadResponse(200,"File downloaded successfully", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return ResponseEntity.ok().body(commonResponseDTO);
    }

    @Async
    public CompletableFuture<String> downloadFileAsync(String bucketName,String key, String downloadDir){
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
    public List<String> downloadMultipleFiles(String bucketName,String prefix) throws ExecutionException, InterruptedException {
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
                fileDownloadFutures.add(downloadFileAsync(bucketName,key, downloadDir));
            }
            else{
                System.out.println(key);
                downloadMultipleFiles(bucketName,key);
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


