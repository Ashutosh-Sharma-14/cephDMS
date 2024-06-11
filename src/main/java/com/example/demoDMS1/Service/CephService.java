package com.example.demoDMS1.Service;

import com.example.demoDMS1.Model.UploadRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public interface CephService {
    String enableVersioning(String bucketName);

    List<String> listVersionOfObject(String bucketName, String objectKey);

    ResponseEntity<?> listBuckets();

    ResponseEntity<List<String>> listObjects(String bucketName, String prefix);

    ResponseEntity<Map<String,Object>> listPaginatedObjects(String bucketName, String prefix,int maxKeys,String continuationToken);

    void listMetadata(String objectKey);

    Map<String, String> addMetadata(Map<String,String> metadata, String bucketName, String objectKey);

    void listTags(String objectKey);

//    void searchWithTags(@RequestParam String tagKey, @RequestParam String tagValue);

    String addTagToObject(String objectKey,String tagKey,String tagValue);

    void deleteObject(String bucketName,String objectKey);

    String uploadFile(MultipartFile file,
                      String fileYear,
                      String bankName,
                      String accountNo) throws IOException;

    ResponseEntity<List<String>> uploadMultipleFiles(UploadRequestDTO uploadRequestDTO) throws IOException, ExecutionException, InterruptedException;

//    List<String> uploadFilesToMultiplePrefixes(@RequestPart("accountFilesRequest")AccountFilesRequest accountFilesRequest) throws ExecutionException, InterruptedException, IOException;

    String downloadFile(String bucketName, String objectKey,
    String versionId) throws IOException;

    List<String> downloadMultipleFiles(String prefix) throws IOException, ExecutionException,InterruptedException;
}
