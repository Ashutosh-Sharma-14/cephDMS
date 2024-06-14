package com.example.demoDMS1.Controller;

import com.example.demoDMS1.Entity.MetadataEntity;
import com.example.demoDMS1.Model.CommonResponseDTO;
import com.example.demoDMS1.Model.DownloadRequestDTO;
import com.example.demoDMS1.Model.UploadRequestDTO;
import com.example.demoDMS1.Service.CephService;
import com.example.demoDMS1.Service.MetadataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final MetadataService metadataService;

    CephController(CephService cephService, MetadataService metadataService){
        this.cephService = cephService;
        this.metadataService = metadataService;
    }

    @GetMapping("/search-objectKey-by-metadata")
    public List<String> findObjectKeysByMetadataExists(@RequestParam String key){
        return metadataService.findObjectKeysByMetadataExists(key);
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

    @GetMapping("/list-objects-by-authority")
    public ResponseEntity<List<String>> listObjectByAuthority(@RequestParam String bucketName, @RequestParam String userRole){
        return cephService.listObjectsByAuthority(bucketName,userRole);
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
    public ResponseEntity<CommonResponseDTO<?>> uploadMultipleFilesToCeph(@RequestPart MultipartFile[] files,
                                                                       @RequestPart String bucketName,
                                                                       @RequestPart String objectKey,
                                                                       @RequestPart String userRole,
                                                                       @RequestPart String metadataJson) throws ExecutionException, InterruptedException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> metadata = objectMapper.readValue(metadataJson, List.class);
        UploadRequestDTO uploadRequestDTO = new UploadRequestDTO(files,bucketName,objectKey,userRole,metadata);
        return cephService.uploadMultipleFiles(uploadRequestDTO);
    }


    @GetMapping("/download-file-from-ceph")
    public ResponseEntity<CommonResponseDTO<?>> downloadFileFromCeph(@RequestBody DownloadRequestDTO downloadRequestDTO) throws IOException {
        return cephService.downloadFile(downloadRequestDTO);
    }

    @GetMapping("/download-multiple-files-from-ceph")
    public List<String> downloadMultipleFilesFromCeph(@RequestParam String prefix) throws IOException, ExecutionException, InterruptedException {
        return cephService.downloadMultipleFiles(prefix);
    }
}
