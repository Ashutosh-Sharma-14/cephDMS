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
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.*;
import java.net.SocketTimeoutException;
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

    @Autowired
    MetadataService metadataService;
    @Autowired
    MetadataRepository metadataRepository;
    @Autowired
    UserRoleService userRoleService;

//    this is private final to make it immutable and thread safe
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private static final long PART_SIZE = 5*1024 * 1024; // 5 MB
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
                    .region(Region.US_EAST_1)
                    .build();

            presigner = S3Presigner.builder()
                    .region(Region.US_EAST_1)
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
    public void uploadFileTesting(String bucketName, String keyName, String filePath) throws IOException{
        keyName = keyName + "-" + counter++;
        File file = new File(filePath);
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
            System.out.println("Uploaded file: " + keyName);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public void uploadFileInParts(String bucketName, String keyName, String filePath) throws IOException {
        File file = new File(filePath);

        // Initiate the multipart upload
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        CreateMultipartUploadResponse createMultipartUploadResponse = s3Client.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = createMultipartUploadResponse.uploadId();

        try {
            // Upload parts
            List<CompletedPart> completedParts = new ArrayList<>();
            long fileLength = file.length();
            long partSize = Math.min(PART_SIZE, fileLength);
            FileInputStream fis = new FileInputStream(file);

            for (int i = 0; i < fileLength; i += partSize) {
                partSize = Math.min(PART_SIZE, fileLength - i);
                byte[] partData = new byte[(int) partSize];
                fis.read(partData);

                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .uploadId(uploadId)
                        .partNumber(completedParts.size() + 1)
                        .build();

                UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest, RequestBody.fromBytes(partData));
                // Debug statement to see which part is uploaded
                System.out.println("Uploaded part " + (completedParts.size() + 1) + ", ETag: " + uploadPartResponse.eTag());

                completedParts.add(CompletedPart.builder()
                        .partNumber(completedParts.size() + 1)
                        .eTag(uploadPartResponse.eTag())
                        .build());
            }
            fis.close();

            // Complete the multipart upload
            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build();

            s3Client.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println("Multipart upload completed.");
        } catch (Exception e) {
            s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .uploadId(uploadId)
                    .build());
            System.out.println("Multipart upload aborted.");
            e.printStackTrace();
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

//    public void deleteAllVersions(String bucketName) {
//        ListObjectVersionsRequest listObjectVersionsRequest = ListObjectVersionsRequest.builder()
//                .bucket(bucketName)
//                .build();
//
//        ListObjectVersionsResponse listObjectVersionsResponse;
//        do {
//            try {
//                listObjectVersionsResponse = s3Client.listObjectVersions(listObjectVersionsRequest);
//                List<ObjectVersion> objectVersions = listObjectVersionsResponse.versions();
//
//                if (!objectVersions.isEmpty()) {
//                    List<ObjectIdentifier> objectsToDelete = objectVersions.stream()
//                            .flatMap(version -> Stream.of(ObjectIdentifier.builder()
//                                    .key(version.key())
//                                    .versionId(version.versionId())
//                                    .build()))
//                            .collect(Collectors.toList());
//
//                    DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
//                            .bucket(bucketName)
//                            .delete(Delete.builder().objects(objectsToDelete).build())
//                            .build();
//
//                    s3Client.deleteObjects(deleteObjectsRequest);
//                }
//
//                listObjectVersionsRequest = listObjectVersionsRequest.toBuilder()
//                        .keyMarker(listObjectVersionsResponse.nextKeyMarker())
//                        .versionIdMarker(listObjectVersionsResponse.nextVersionIdMarker())
//                        .build();
//            } catch (AwsServiceException | SdkClientException e) {
//                throw new RuntimeException(e);
//            }
//        } while (listObjectVersionsResponse.isTruncated());
//    }

    @Override
    public void deleteAllVersions(String bucketName, String objectKey) {

        try {
            // List all versions of objects with the specified prefix
            ListObjectVersionsRequest listObjectVersionsRequest = ListObjectVersionsRequest.builder()
                    .bucket(bucketName)
                    .prefix(objectKey)
                    .build();

            ListObjectVersionsResponse listObjectVersionsResponse;
            do {
                listObjectVersionsResponse = s3Client.listObjectVersions(listObjectVersionsRequest);
                List<ObjectVersion> objectVersions = listObjectVersionsResponse.versions();

                // Prepare a batch delete request for all object versions
                List<ObjectIdentifier> objectsToDelete = new ArrayList<>();
                for (ObjectVersion objectVersion : objectVersions) {
                    objectsToDelete.add(ObjectIdentifier.builder()
                            .key(objectVersion.key())
                            .versionId(objectVersion.versionId())
                            .build());
                }

                if (!objectsToDelete.isEmpty()) {
                    DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                            .bucket(bucketName)
                            .delete(Delete.builder()
                                    .objects(objectsToDelete)
                                    .build())
                            .build();
                    s3Client.deleteObjects(deleteObjectsRequest);
                    System.out.println("Deleted versions: " + objectsToDelete.size());
                }

                listObjectVersionsRequest = listObjectVersionsRequest.toBuilder()
                        .keyMarker(listObjectVersionsResponse.nextKeyMarker())
                        .versionIdMarker(listObjectVersionsResponse.nextVersionIdMarker())
                        .build();

            } while (listObjectVersionsResponse.isTruncated());

        } catch (S3Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public ResponseEntity<?> deleteBucket(String bucketName){
        try{
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest
                    .builder()
                    .bucket(bucketName)
                    .build();

           s3Client.deleteBucket(deleteBucketRequest);
        }
        catch (NoSuchBucketException e) {
            // Handle the case where the bucket does not exist
            System.err.println("Bucket does not exist: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (S3Exception e) {
            // Handle other S3 specific exceptions
            System.err.println("S3 Exception: " + e.awsErrorDetails().errorMessage());
            throw e;
        } catch (SdkClientException e) {
            // Handle client-side errors (e.g., network issues)
            System.err.println("SDK Client Exception: " + e.getMessage());
            throw e;
        }
        return new ResponseEntity<>("Bucket: " + bucketName + " deleted successfully",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> isVersioningEnabled(String bucketName){
        try{
            GetBucketVersioningRequest getBucketVersioningRequest = GetBucketVersioningRequest.builder()
                    .bucket(bucketName)
                    .build();

            BucketVersioningStatus currentStatus = s3Client.getBucketVersioning(getBucketVersioningRequest).status();
            System.out.println(currentStatus);
            return new ResponseEntity<>(currentStatus,HttpStatus.OK);
        }
        catch (NoSuchBucketException e){
            return new ResponseEntity<>("No bucketname: " + bucketName,HttpStatus.BAD_REQUEST);
        }
//        if(currentStatus == BucketVersioningStatus.ENABLED) return new ResponseEntity<>(true,HttpStatus.OK);
//        else return new ResponseEntity<>(false,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> changeVersioningStatus(String bucketName) {
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
//            System.out.println("Bucket versioning enabled for bucket: " + bucketName);
            return new ResponseEntity<>(true,HttpStatus.OK);
        } else {
            PutBucketVersioningRequest putBucketVersioningRequest = PutBucketVersioningRequest.builder()
                    .bucket(bucketName)
                    .versioningConfiguration(conf -> conf.status(BucketVersioningStatus.SUSPENDED))
                    .build();

            s3Client.putBucketVersioning(putBucketVersioningRequest);
            return new ResponseEntity<>(false,HttpStatus.OK);
        }
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
    public ResponseEntity<?> addBucketTags(String bucketName, Map<String, String> tags) throws SocketTimeoutException {
        List<Tag> tagSet = new ArrayList<>();
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            tagSet.add(Tag.builder().key(entry.getKey()).value(entry.getValue()).build());
        }

        try{
            PutBucketTaggingRequest putBucketTaggingRequest = PutBucketTaggingRequest.builder()
                    .bucket(bucketName)
                    .tagging(Tagging.builder().tagSet(tagSet).build())
                    .build();
            s3Client.putBucketTagging(putBucketTaggingRequest);
        }
        catch (S3Exception e){
            System.out.println("Error details: " + e.awsErrorDetails().errorMessage());
        }
        return new ResponseEntity<>("Added tags to bucket: " + bucketName,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> listBuckets() {
        ListBucketsResponse response = null;
        try {
            response = s3Client.listBuckets(ListBucketsRequest.builder().build());
        } catch (S3Exception e) {
            System.out.println(e.awsErrorDetails().errorMessage());
        } catch (SdkClientException e) {
            System.out.println(e.getMessage());
        }
        assert response != null;
        List<Bucket> buckets = response.buckets();
        List<Map<String, String>> bucketResponse = new ArrayList<>();

        System.out.println("Listing buckets:");
        for( Bucket bucket: buckets){
            Map<String, String> bucketData = new HashMap<>();
            bucketData.put("bucketName",bucket.name());
            bucketData.put("creationDate",bucket.creationDate().toString());
            bucketResponse.add(bucketData);
            System.out.println(bucket);
        }

        if(buckets.isEmpty()){
            return ResponseEntity.ok().body("No buckets present");
        }
        else{
            return ResponseEntity.ok().body(bucketResponse);
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

        String uuid = metadataRepository.findUUIDByObjectKey(objectKey);
        System.out.println("uuid: " + uuid);
        String prefix = extractPrefix(objectKey);
        System.out.println("prefix:" + prefix);
        String key = prefix + uuid;
        System.out.println("key: " + key);

        GetObjectTaggingRequest getObjectTaggingRequest = GetObjectTaggingRequest.builder()
                .bucket(bucketName)
                .key(key)
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

    public String getObjectUUID(String key) {
        return metadataRepository.findUUIDByObjectKey(key);
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

        for (int i = 0; i < files.length; i++) {
            Map<String,String> metadata = metadataList.get(i);
            String prefix = uploadRequestDTO.getObjectKey();
            String objectKey = uploadRequestDTO.getObjectKey() + files[i].getOriginalFilename();
            System.out.println("Object key to be checked in mongo: " + objectKey);
            System.out.println("prefix in the upload method: " + prefix);
            String uuid = null;
            String key = null;
            if(metadataRepository.doesKeyExist(objectKey)){
                uuid = getObjectUUID(objectKey);
                key = prefix + uuid;
            }
            else{
                uuid = UUID.randomUUID().toString();
                key = uploadRequestDTO.getObjectKey() + uuid;
            }
            System.out.println("key that is sent to upload file method: " + key);
//            saving the required data in mongodb collection to implement search
            MetadataEntity metadataEntity = new MetadataEntity();
            metadataEntity.setUuid(uuid);
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

    public static String extractPrefix(String objectKey) {
        int lastSlashIndex = objectKey.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return ""; // No slash found, return empty string
        }
        return objectKey.substring(0, lastSlashIndex + 1); // Include the last slash in the prefix
    }

    @Override
    public ResponseEntity<CommonResponseDTO<?>> downloadFile(DownloadRequestDTO downloadRequestDTO) {
        String downloadDestination = System.getProperty("user.home") + "/Desktop/";
        System.out.println("bucketName:" + downloadRequestDTO.getBucketName());
        String bucketName = downloadRequestDTO.getBucketName();
        String objectKey = downloadRequestDTO.getObjectKey();
        System.out.println("Received objectkey :" + downloadRequestDTO.getObjectKey());
        System.out.println("Object key: "+ objectKey);

        File downloadDir = new File(downloadDestination);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
        }

        String uuid = metadataRepository.findUUIDByObjectKey(objectKey);
        System.out.println("uuid: " + uuid);
        String prefix = extractPrefix(objectKey);
        System.out.println("prefix:" + prefix);
        String key = prefix + uuid;
        System.out.println("key: " + key);

        String filename = Paths.get(key).getFileName().toString();

        try{
            System.out.println("going in try block");
//            check if buffering option is available for downloading large files
//            Answer: The buffering is internally managed by S3Client, but if customization is required we need to use get the inputStream and buffe

            File file = new File(downloadDir, filename);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .versionId(downloadRequestDTO.getVersionId())
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

//            s3Client.getObject(
////                    lambda expression. Curly brackets can be removed in case of single statement
//                    req -> req.bucket("perfios1")
//                            .key(key),
//                    Paths.get(downloadDestination+key)
//            );
        }
        catch (Exception e){
            e.getStackTrace();
        }
        File downloadedFile = new File(downloadDestination+filename);
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

            System.out.println("key: " + key);
            System.out.println("bucketName: " + bucketName);

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


