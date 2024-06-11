package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponseDTO<T> {
    private int statusCode;
    private String responseMessage;
    private String timestamp;
    private T data;
}
