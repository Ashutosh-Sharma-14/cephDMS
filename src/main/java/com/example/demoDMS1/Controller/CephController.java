package com.example.demoDMS1.Controller;

import com.example.demoDMS1.Entity.MetadataEntity;
import com.example.demoDMS1.Model.CommonResponseDTO;
import com.example.demoDMS1.Model.DownloadRequestDTO;
import com.example.demoDMS1.Model.ListPaginatedObjectsResponse;
import com.example.demoDMS1.Model.UploadRequestDTO;
import com.example.demoDMS1.Service.CephService;
import com.example.demoDMS1.Service.MetadataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Entity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/user")
public class CephController {
    private final CephService cephService;
    private final MetadataService metadataService;

    CephController(CephService cephService, MetadataService metadataService){
        this.cephService = cephService;
        this.metadataService = metadataService;
    }

    @GetMapping("/test-file-upload")
    public void uploadFileTesting() throws IOException{
        cephService.uploadFileTesting("test1","testing-file","/home/ashutosh.sharma/Documents/ceph-dms");
    }

    @PostMapping("/upload-file-in-parts")
    public void uploadFileInParts(@RequestParam String bucketName, @RequestParam String keyName, @RequestParam String filePath) throws IOException {
        cephService.uploadFileInParts(bucketName,keyName,filePath);
    }

    @PutMapping("/create-bucket")
    public ResponseEntity<?> createBucket(@RequestParam String bucketName){
        return cephService.createBucket(bucketName);
    }

    @DeleteMapping("/delete-bucket")
    public ResponseEntity<?> deleteBucket(@RequestParam String bucketName){
        return cephService.deleteBucket(bucketName);
    }

    @GetMapping("/search-objectKey-by-metadata")
    public List<String> findObjectKeysByMetadataExists(@RequestParam String key, @RequestParam String value){
        return metadataService.findObjectKeysByMetadata(key,value);
    }

    @GetMapping("/is-versioning-enabled")
    public ResponseEntity<?> isVersioningEnabled(@RequestParam String bucketName){
        return cephService.isVersioningEnabled(bucketName);
    }

    @PostMapping("/change-versioning")
    public ResponseEntity<?>  changeVersioningStatus(@RequestParam String bucketName) {
        return cephService.changeVersioningStatus(bucketName);
    }

    @GetMapping("/list-object-versions")
    public ResponseEntity<List<String>> listObjectVersionInfo(@RequestParam String bucketName, @RequestParam String objectKey) {
        return cephService.listVersionOfObject(bucketName,objectKey);
    }

    @GetMapping("/list-buckets")
    public ResponseEntity<?> listBuckets() {
        return cephService.listBuckets();
    }

    @GetMapping("/list-objects")
    public ResponseEntity<?> listObjects(@RequestParam String bucketName,@RequestParam  String prefix){
        return cephService.listObjects(bucketName, prefix);
    }

    @GetMapping("/list-objects-by-authority")
    public ResponseEntity<List<String>> listObjectByAuthority(@RequestParam String bucketName, @RequestParam String userRole){
        return cephService.listObjectsByAuthority(bucketName,userRole);
    }


    @GetMapping("/list-objects-paginated")
    public ResponseEntity<ListPaginatedObjectsResponse> listPaginatedObjects(@RequestParam String bucketName, @RequestParam String prefix, @RequestParam int maxKeys, @RequestParam String continuationToken){
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

    @GetMapping("/list-objects-by-authority-tag")
    public ResponseEntity<List<String>> listObjectsByAuthorityTag(@RequestParam String bucketName, @RequestParam String userRole){
        return cephService.listObjectByAuthorityTags(bucketName,userRole);
    }

    @PostMapping("/modify-object-tag")
    public String modifyObjectTag(@RequestParam String objectKey, @RequestParam String tagKey, @RequestParam String tagValue){
        return cephService.modifyObjectTag(objectKey, tagKey,tagValue);
    }

    @PostMapping("/add-tag-to-object")
    public ResponseEntity<String> addTagToObject(@RequestParam String bucketName, @RequestParam String objectKey,@RequestParam String tagKey, @RequestParam String tagValue){
        return cephService.addTagToObject(bucketName,objectKey,tagKey,tagValue);
    }

    @DeleteMapping("/delete-object")
    public void deleteObject(@RequestParam String bucketName,@RequestParam String objectKey){
        cephService.deleteObject(bucketName,objectKey);
    }

    @PostMapping("/upload-file-to-ceph")
    public String uploadFileToCeph(@RequestParam MultipartFile file,
                                   @RequestParam String bucketName,
                                   @RequestParam String objectKey,
                                   @RequestParam String userRole,
                                   @RequestParam String metadataJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> metadata = objectMapper.readValue(metadataJson, new TypeReference<Map<String, String>>() {});
        System.out.println("trying to print metadata" + metadata);
        return cephService.uploadFile(file,bucketName,objectKey,userRole,metadata);
    }

    @PostMapping("/upload-multiple-files-to-ceph")
    public ResponseEntity<CommonResponseDTO<?>> uploadMultipleFilesToCeph(@RequestParam ("multipartFiles") MultipartFile[] files,
                                                                       @RequestParam String bucketName,
                                                                       @RequestParam String objectKey,
                                                                       @RequestParam String userRole,
                                                                       @RequestParam String metadataJson) throws ExecutionException, InterruptedException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> metadata = objectMapper.readValue(metadataJson, List.class);
        UploadRequestDTO uploadRequestDTO = new UploadRequestDTO(files,bucketName,objectKey,userRole,metadata);
        return cephService.uploadMultipleFiles(uploadRequestDTO);
    }


    @GetMapping("/download-file-from-ceph")
    public ResponseEntity<CommonResponseDTO<?>> downloadFileFromCeph(@RequestParam String bucketName,@RequestParam String objectKey, @RequestParam String versionId) throws IOException {
        DownloadRequestDTO downloadRequestDTO = new DownloadRequestDTO(bucketName,objectKey,versionId);
        return cephService.downloadFile(downloadRequestDTO);
    }

    @GetMapping("/download-multiple-files-from-ceph")
    public List<String> downloadMultipleFilesFromCeph(@RequestParam String bucketName,@RequestParam String prefix) throws IOException, ExecutionException, InterruptedException {
        return cephService.downloadMultipleFiles(bucketName,prefix);
    }
}
