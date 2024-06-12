package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponseDTO {
    private boolean isRegistered;
    private String message;

    public RegistrationResponseDTO(String message) {
        this.message = message;
    }
}
