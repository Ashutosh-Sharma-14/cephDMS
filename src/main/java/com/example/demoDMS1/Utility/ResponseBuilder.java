package com.example.demoDMS1.Utility;

import com.example.demoDMS1.Model.CommonResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseBuilder {
    public static <T> CommonResponseDTO<T> buildUploadResponse(int statusCode, String message, String  timestamp, T data){
        return new CommonResponseDTO<>(statusCode,message,timestamp,data);
    }

    public static <T> CommonResponseDTO<T> buildDownloadResponse(int statusCode, String message, String timeStamp){
        return new CommonResponseDTO<>(statusCode,message,timeStamp,null);
    }
}
