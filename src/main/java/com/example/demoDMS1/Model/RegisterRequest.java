package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String userRole;
    private String userEmail;
    private String userPassword;
    private String userAuthorityLevel;
}
