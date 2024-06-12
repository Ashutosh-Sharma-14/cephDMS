package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequestDTO {
    MultipartFile[] multipartFiles;
    String bucketName;
    String objectKey;
    List<Map<String, String>> metadata;
}
