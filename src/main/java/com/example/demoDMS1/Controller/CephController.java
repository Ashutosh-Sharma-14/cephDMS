package com.example.demoDMS1.Controller;

import com.example.demoDMS1.Model.UploadRequestDTO;
import com.example.demoDMS1.Service.CephService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.services.s3.model.S3Object;
//import software.amazon.awssdk.services.s3.model.Tag;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class CephController {
    private final CephService cephService;

    CephController(CephService cephService){
        this.cephService = cephService;
    }

    @PostMapping("/enable-versioning")
    public String enableVersioning(@RequestParam String bucketName) {
        return cephService.enableVersioning(bucketName);
    }

    @GetMapping("/list-object-versions")
    public List<String> listObjectVersionInfo(@RequestParam String bucketName, @RequestParam String objectKey) {
        return cephService.listVersionOfObject(bucketName,objectKey);
    }

    @GetMapping("/list-buckets")
    public ResponseEntity<?> listBuckets() {
        return cephService.listBuckets();
    }

    @GetMapping("/list-objects")
    public ResponseEntity<List<String>> listObjects(String bucketName, String prefix){
        return cephService.listObjects(bucketName, prefix);
    }

    @GetMapping("/list-objects-paginated")
    public ResponseEntity<Map<String, Object>> listPaginatedObjects(String bucketName, String prefix, int maxKeys, String continuationToken){
        return cephService.listPaginatedObjects(bucketName,prefix,maxKeys,continuationToken);
    }

    @GetMapping("/list-metadata")
    public void listMetadata(String objectKey){
        cephService.listMetadata(objectKey);
    }

    @GetMapping("/list-tags")
    public void listTags(String objectKey){
        cephService.listTags(objectKey);
    }

    @PostMapping("/add-tag-to-object")
    public String addTagToObject(String objectKey, String tagKey, String tagValue){
        return cephService.addTagToObject(objectKey, tagKey,tagValue);
    }

    @DeleteMapping("/delete-object")
    public void deleteObject(String bucketName, String objectKey){
        cephService.deleteObject(bucketName,objectKey);
    }

    @PostMapping("/upload-file-to-ceph")
    public String uploadFileToCeph(MultipartFile file, String fileYear, String bankName, String accountNo) throws IOException {
        return cephService.uploadFile(file,fileYear,bankName,accountNo);
    }

    @PostMapping("/upload-multiple-files-to-ceph")
    public ResponseEntity<List<String>> uploadMultipleFilesToCeph(UploadRequestDTO uploadRequestDTO) throws ExecutionException, InterruptedException, IOException {
        return cephService.uploadMultipleFiles(uploadRequestDTO);
    }

//    @PostMapping("/upload-files-by-account")
//    public List<String> uploadFilesByAccount(@RequestPart("accountFilesRequest") AccountFilesRequest accountFilesRequest) throws ExecutionException, InterruptedException, IOException {
//        return cephService.uploadFilesToMultiplePrefixes(accountFilesRequest);
//    }

    @GetMapping("/download-file-from-ceph")
    public String downloadFileFromCeph(String prefix, String versionId) throws IOException {
        return cephService.downloadFile(prefix,versionId);
    }

    @GetMapping("/download-multiple-files-from-ceph")
    public List<String> downloadMultipleFilesFromCeph(String prefix) throws IOException, ExecutionException, InterruptedException {
        return cephService.downloadMultipleFiles(prefix);
    }
}
