package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponseDTO<T> {
    private int statusCode;
    private String responseMessage;
    private String timestamp;
    private T data;


    public CommonResponseDTO(int statusCode,String responseMessage) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
    }

    public CommonResponseDTO(int statusCode,String responseMessage,String timestamp) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.timestamp = timestamp;
    }

}
