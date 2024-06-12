package com.example.demoDMS1.Service;

import com.example.demoDMS1.Model.LoginForm;
import com.example.demoDMS1.Model.RegistrationForm;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("employeeService")
public interface UserService {
    ResponseEntity<?> registerUser(RegistrationForm registrationForm);
    ResponseEntity<?> authenticateUser(LoginForm loginForm);
    boolean checkIfUserExists(String employeeEmail);
}
