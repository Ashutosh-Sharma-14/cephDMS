package com.example.demoDMS1.Utility;

import com.example.demoDMS1.Model.CommonResponseDTO;

import java.time.LocalDateTime;

public class ResponseBuilder {
    public static <T> CommonResponseDTO<T> buildSResponse(int statusCode, String message, LocalDateTime timestamp, T data){
        return new CommonResponseDTO<>(statusCode,message,timestamp,data);
    }
}
