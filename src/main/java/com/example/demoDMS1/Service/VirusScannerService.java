//package com.example.demoDMS1.Service;
//
//import org.hibernate.boot.archive.scan.spi.ScanResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//public class VirusScannerService {
//    private final ClamClient clamClient;
//
//    @Autowired
//    public VirusScannerService(ClamClient clamClient) {
//        this.clamClient = clamClient;
//    }
//    public boolean isSafe(byte[] fileBytes) throws IOException {
//        ScanResult scanResult = clamClient.scanFile(fileBytes);
//    }
//}
