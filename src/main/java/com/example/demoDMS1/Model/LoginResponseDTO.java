package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private boolean isLoggedIn;
    private String userEmail;
    private String userRole;
    private String message;

    public LoginResponseDTO(boolean isLoggedIn, String message) {
        this.isLoggedIn = isLoggedIn;
        this.message = message;
    }
}
