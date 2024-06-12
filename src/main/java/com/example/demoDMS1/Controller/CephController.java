package com.example.demoDMS1.Controller;

import com.example.demoDMS1.Model.CommonResponseDTO;
import com.example.demoDMS1.Model.DownloadRequestDTO;
import com.example.demoDMS1.Model.UploadRequestDTO;
import com.example.demoDMS1.Service.CephService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.Response;
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
    public ResponseEntity<List<String>> listObjects(@RequestParam String bucketName,@RequestParam  String prefix){
        return cephService.listObjects(bucketName, prefix);
    }

    @GetMapping("/list-objects-paginated")
    public ResponseEntity<Map<String, Object>> listPaginatedObjects(@RequestParam String bucketName, @RequestParam String prefix, @RequestParam int maxKeys, @RequestParam String continuationToken){
        return cephService.listPaginatedObjects(bucketName,prefix,maxKeys,continuationToken);
    }

    @GetMapping("/list-metadata")
    public ResponseEntity<Map<String,String>> listMetadata(@RequestParam String objectKey){
        return cephService.listMetadata(objectKey);
    }

    @GetMapping("/list-tags")
    public void listTags(@RequestParam String objectKey){
        cephService.listTags(objectKey);
    }

    @PostMapping("/add-tag-to-object")
    public String addTagToObject(@RequestParam String objectKey, @RequestParam String tagKey, @RequestParam String tagValue){
        return cephService.addTagToObject(objectKey, tagKey,tagValue);
    }

    @DeleteMapping("/delete-object")
    public void deleteObject(@RequestParam String bucketName,@RequestParam String objectKey){
        cephService.deleteObject(bucketName,objectKey);
    }

    @PostMapping("/upload-file-to-ceph")
    public String uploadFileToCeph(@RequestParam MultipartFile file, @RequestParam String fileYear, @RequestParam String bankName, @RequestParam String accountNo) throws IOException {
        return cephService.uploadFile(file,fileYear,bankName,accountNo);
    }

    @PostMapping("/upload-multiple-files-to-ceph")
    public ResponseEntity<CommonResponseDTO<?>> uploadMultipleFilesToCeph(@RequestPart MultipartFile[] files,
                                                                       @RequestPart String bucketName,
                                                                       @RequestPart String objectKey,
                                                                       @RequestPart @RequestBody List<Map<String,String>> metadata) throws ExecutionException, InterruptedException, IOException {
        UploadRequestDTO uploadRequestDTO = new UploadRequestDTO(files,bucketName,objectKey,metadata);
        return cephService.uploadMultipleFiles(uploadRequestDTO);
    }

//    @PostMapping("/upload-files-by-account")
//    public List<String> uploadFilesByAccount(@RequestPart("accountFilesRequest") AccountFilesRequest accountFilesRequest) throws ExecutionException, InterruptedException, IOException {
//        return cephService.uploadFilesToMultiplePrefixes(accountFilesRequest);
//    }

    @GetMapping("/download-file-from-ceph")
    public ResponseEntity<CommonResponseDTO<?>> downloadFileFromCeph(@RequestBody DownloadRequestDTO downloadRequestDTO) throws IOException {
        return cephService.downloadFile(downloadRequestDTO);
    }

    @GetMapping("/download-multiple-files-from-ceph")
    public List<String> downloadMultipleFilesFromCeph(@RequestParam String prefix) throws IOException, ExecutionException, InterruptedException {
        return cephService.downloadMultipleFiles(prefix);
    }
}
