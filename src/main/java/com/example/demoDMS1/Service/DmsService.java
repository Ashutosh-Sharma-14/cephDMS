package com.example.demoDMS1.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface DmsService {
    String uploadFile(MultipartFile file,
                      @RequestParam String fileYear,
                      @RequestParam String bankName,
                      @RequestParam String accountNo) throws IOException;

    String uploadMultipleFiles(MultipartFile[] files,
                      @RequestParam String fileYear,
                      @RequestParam String bankName,
                      @RequestParam String accountNo) throws IOException;

    String downloadFile(@RequestParam String fileYear,
                        @RequestParam String bankName,
                        @RequestParam String accountNo,
                        @RequestParam String fileName) throws IOException;
}
