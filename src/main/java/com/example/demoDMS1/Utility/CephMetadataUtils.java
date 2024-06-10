package com.example.demoDMS1.Utility;

import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;

import java.util.Map;

public class CephMetadataUtils {
    public static HeadObjectRequest createHeadObjectRequest(String bucketName, String key){

        return HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
    }

    public static void printMetadata(HeadObjectResponse headObjectResponse){
        System.out.println("Metadata:");
        for (Map.Entry<String, String> entry : headObjectResponse.metadata().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
