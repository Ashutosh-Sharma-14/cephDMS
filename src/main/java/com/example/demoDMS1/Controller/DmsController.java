package com.example.demoDMS1.Controller;

import com.example.demoDMS1.Service.DmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class DmsController {

    @Autowired
    private DmsService dmsService;

    @PostMapping("/uploadFile")
    public String uploadFileToLocal(@RequestParam("file") MultipartFile file,
                                    @RequestParam String fileYear,
                                    @RequestParam String bankName,
                                    @RequestParam String accountNo) throws IOException {
        return dmsService.uploadFile(file,fileYear, bankName, accountNo);
    }

    @PostMapping("/uploadMultipleFiles")
    public String uploadFileToLocal(@RequestParam("files") MultipartFile[] files,
                                    @RequestParam String fileYear,
                                    @RequestParam String bankName,
                                    @RequestParam String accountNo) throws IOException {
        return dmsService.uploadMultipleFiles(files,fileYear, bankName, accountNo);
    }

    @GetMapping("/downloadFile")
    public String downloadFileToLocal( @RequestParam String fileName,
                                       @RequestParam String fileYear,
                                       @RequestParam String bankName,
                                       @RequestParam String accountNo
                                     ) throws IOException{
        return dmsService.downloadFile(fileYear,bankName,accountNo,fileName);
    }
}
