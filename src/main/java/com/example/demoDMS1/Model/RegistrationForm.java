package com.example.demoDMS1.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationForm {
    private String userId;
    private String employeeName;
    private String employeeRole;
    private String employeeEmail;
    private String employeePassword;
}
