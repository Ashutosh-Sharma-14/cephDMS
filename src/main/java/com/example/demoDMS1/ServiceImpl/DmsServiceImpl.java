package com.example.demoDMS1.ServiceImpl;

import com.example.demoDMS1.Service.DmsService;
import org.apache.tika.metadata.Metadata;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;

import static com.example.demoDMS1.Utility.MetadataUtils.*;

@Component
public class DmsServiceImpl implements DmsService {

    String homeDestination = System.getProperty("user.home");

    @Override
    public String uploadFile(MultipartFile file,
                             String fileYear,
                             String bankName,
                             String accountNo) throws IOException {
        long fileSize = file.getSize();
        String fileName = file.getOriginalFilename();
        String uploadPath = homeDestination + "/Desktop/" + fileYear + "/" + bankName +  "/" + accountNo;
        System.out.println("bankName: " + bankName);
        System.out.println(uploadPath);

        if(!file.isEmpty() && fileSize <= 268435456){
            try(InputStream inputStream = file.getInputStream()) {

                Metadata metadata = MetadataOperations(inputStream,uploadPath,fileName);

//              create directory if it doesn't exist
                File directory = new File(uploadPath);
                directory.mkdirs();

                // Create a FileOutputStream to write the file
                try (FileOutputStream outputStream = new FileOutputStream(uploadPath + "/" + fileName)) {
                    // Read from input stream and write to output stream byte by byte
                    int byteRead;
                    while ((byteRead = inputStream.read()) != -1) {
                        outputStream.write(byteRead);
                    }
                }
                catch(Exception e){
                    System.out.println(Arrays.toString(e.getStackTrace()));
                }
                return fileName + " uploaded successfully with metadata " + metadata;
            } catch (Exception e) {
                return e.toString();
            }
        }
        else{
            File directory = new File(uploadPath);
            directory.mkdirs();

            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = new FileOutputStream(uploadPath + "/" + fileName)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return fileName + "uploaded successfully";
        }
//        return "please select a file to upload";
    }

    @Override
    public String uploadMultipleFiles(MultipartFile[] files,
                             String fileYear,
                             String bankName,
                             String accountNo) throws IOException{
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            String uploadPath = homeDestination + "/Desktop/" + fileYear + "/" + bankName + "/" + accountNo;

                File directory = new File(uploadPath);
                directory.mkdirs();

                try (InputStream inputStream = file.getInputStream();
                     OutputStream outputStream = new FileOutputStream(uploadPath + "/" + fileName)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
        }
        StringBuilder result = new StringBuilder();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();
            result.append(fileName).append(" uploaded successfully. ");
        }
        return result.toString();
    }

    @Override
    public String downloadFile(
            String fileName,
            String fileYear,
            String bankName,
            String accountNo
    ) throws IOException{
        String downloadDestination = homeDestination + "/Downloads/"+fileName;
        String filePath = homeDestination + "/Desktop/" + fileYear + "/" + bankName + "/" + accountNo + "/" + fileName;

        File fileToBeDownloaded = new File(filePath);
        if(!fileToBeDownloaded.exists()){
            return "file do not exist";
        }
        else{
            try(InputStream inputStream = new BufferedInputStream(new FileInputStream(fileToBeDownloaded));
                OutputStream outputStream = new FileOutputStream(downloadDestination)){
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return fileName + "uploaded successfully";
        }
    }
}
