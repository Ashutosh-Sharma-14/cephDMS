package com.example.demoDMS1.Service;

import com.example.demoDMS1.Model.LoginForm;
import com.example.demoDMS1.Model.RegistrationForm;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("employeeService")
public interface UserService {
    ResponseEntity<?> registerEmployee(RegistrationForm registrationForm);
    ResponseEntity<?> authenticateEmployee(LoginForm loginForm);
    boolean checkIfEmployeeExists(String employeeEmail);
}
