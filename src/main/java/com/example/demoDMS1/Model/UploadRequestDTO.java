package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.multi.MultiListUI;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadRequestDTO {
    MultipartFile[] multipartFiles;
    String bucketName;
    String objectKey;
    Map<String, String> metadata;
}
