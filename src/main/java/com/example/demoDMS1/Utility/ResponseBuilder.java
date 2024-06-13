package com.example.demoDMS1.Utility;

import com.example.demoDMS1.Model.CommonResponseDTO;
import com.example.demoDMS1.Model.LoginResponseDTO;
import com.example.demoDMS1.Model.RegistrationResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseBuilder {
    public static <T> CommonResponseDTO<T> buildUploadResponse(int statusCode, String message, String  timestamp, T data){
        return new CommonResponseDTO<>(statusCode,message,timestamp,data);
    }

    public static <T> CommonResponseDTO<T> successfulDownloadResponse(int statusCode, String message, String timeStamp){
        return new CommonResponseDTO<>(statusCode,message,timeStamp);
    }

    public static <T> CommonResponseDTO<T> unsuccessfulDownloadResponse(int statusCode, String message){
        return new CommonResponseDTO<>(statusCode,message);
    }

    public static RegistrationResponseDTO successFullRegistrationResponse(boolean isRegistered, String message){
        return new RegistrationResponseDTO(isRegistered,message);
    }

    public static RegistrationResponseDTO failedRegistrationResponse(String message){
        return new RegistrationResponseDTO(message);
    }

    public static LoginResponseDTO successfulLoginResponse(boolean isLoggedIn,String userEmail, String userRole,String message){
        return new LoginResponseDTO(isLoggedIn,userEmail,userRole,message);
    }

    public static LoginResponseDTO failedLoginResponse(boolean isLoggedIn, String message){
        return new LoginResponseDTO(isLoggedIn,message);
    }
}
