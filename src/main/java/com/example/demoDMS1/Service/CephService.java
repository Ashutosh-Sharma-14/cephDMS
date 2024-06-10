package com.example.demoDMS1.Service;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
//import software.amazon.awssdk.services.s3.model.S3Object;
//import software.amazon.awssdk.services.s3.model.Tag;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public interface CephService {
    String enableVersioning(@RequestParam String bucketName);

    List<String> listVersionOfObject(@RequestParam String bucketName, @RequestParam String objectKey);

    ResponseEntity<?> listBuckets();

    ResponseEntity<List<String>> listObjects(@RequestParam String bucketName, @RequestParam String prefix);

    ResponseEntity<Map<String,Object>> listPaginatedObjects(@RequestParam String bucketName, @RequestParam String prefix, @RequestParam int maxKeys, @RequestParam String continuationToken);

    void listMetadata(@RequestParam String objectKey);

    void listTags(@RequestParam String objectKey);

//    void searchWithTags(@RequestParam String tagKey, @RequestParam String tagValue);

    String addTagToObject(@RequestParam String objectKey, @RequestParam String tagKey, @RequestParam String tagValue);

    void deleteObject(@RequestParam String bucketName,@RequestParam String objectKey);

    String uploadFile(@RequestParam MultipartFile file,
                      @RequestParam String fileYear,
                      @RequestParam String bankName,
                      @RequestParam String accountNo) throws IOException;

    List<String> uploadMultipleFiles(@RequestParam MultipartFile[] files,
                                     @RequestParam String prefix) throws IOException, ExecutionException, InterruptedException;

//    List<String> uploadFilesToMultiplePrefixes(@RequestPart("accountFilesRequest")AccountFilesRequest accountFilesRequest) throws ExecutionException, InterruptedException, IOException;

    String downloadFile(@RequestParam String prefix,
    @RequestParam String versionId) throws IOException;

    List<String> downloadMultipleFiles(@RequestParam String prefix) throws IOException, ExecutionException,InterruptedException;
}
