package com.example.demoDMS1.Service;

import com.example.demoDMS1.Model.CommonResponseDTO;
import com.example.demoDMS1.Model.DownloadRequestDTO;
import com.example.demoDMS1.Model.ListPaginatedObjectsResponse;
import com.example.demoDMS1.Model.UploadRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public interface CephService {
    void uploadFileTesting(String bucketName, String keyName, String filePath) throws IOException;

    void uploadFileInParts(String bucketName, String keyName, String filePath) throws IOException;

    ResponseEntity<?> createBucket(String bucketName);

    ResponseEntity<?> deleteBucket(String bucketName);

//    public ResponseEntity<Boolean>  enableVersioning(String bucketName);

    ResponseEntity<?> isVersioningEnabled(String bucketName);

    ResponseEntity<?> changeVersioningStatus(String bucketName);

    ResponseEntity<List<String>> listVersionOfObject(String bucketName, String objectKey);

    ResponseEntity<?> addBucketTags(String bucketName, Map<String, String> tags) throws SocketTimeoutException;

    ResponseEntity<?> listBuckets();

    ResponseEntity<List<String>> listObjectsByAuthority(String bucketName, String userRole);

    ResponseEntity<?> listObjects(String bucketName, String prefix);

    ResponseEntity<ListPaginatedObjectsResponse> listPaginatedObjects(String bucketName, String prefix, int maxKeys, String continuationToken);

//    there can be a method to extract some metadata without uploading file to ceph.

    ResponseEntity<Map<String,String>> listMetadata(String objectKey);

    ResponseEntity<Map<String,String>> listSystemGeneratedMetadata(String objectKey);

    Map<String, String> addMetadata(Map<String,String> metadata, String bucketName, String objectKey);

    Map<String, String> addToExistingMetadata(Map<String, String> metadata, String bucketName, String objectKey);

    void listTags(String objectKey);

    ResponseEntity<List<String>> listObjectByAuthorityTags(String bucketName, String userRole);

//    void searchWithTags(@RequestParam String tagKey, @RequestParam String tagValue);

    String modifyObjectTag(String objectKey,String tagKey,String tagValue);

    ResponseEntity<String> addTagToObject(String bucketName, String objectKey, String tagKey, String tagValue);

    void deleteObject(String bucketName,String objectKey);

    String uploadFile(MultipartFile file,
                      String bucketName,
                      String objectKey,
                      String userRole,
                      Map<String,String> metadataJson) throws IOException;

    ResponseEntity<CommonResponseDTO<?>> uploadMultipleFiles(UploadRequestDTO uploadRequestDTO) throws IOException, ExecutionException, InterruptedException;

//    List<String> uploadFilesToMultiplePrefixes(@RequestPart("accountFilesRequest")AccountFilesRequest accountFilesRequest) throws ExecutionException, InterruptedException, IOException;

    ResponseEntity<CommonResponseDTO<?>> downloadFile(DownloadRequestDTO downloadRequestDTO) throws IOException;

    List<String> downloadMultipleFiles(String bucketName, String prefix) throws IOException, ExecutionException,InterruptedException;
}
