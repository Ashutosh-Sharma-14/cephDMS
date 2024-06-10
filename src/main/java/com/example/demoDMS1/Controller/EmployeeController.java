package com.example.demoDMS1.Controller;

import com.example.demoDMS1.Model.LoginForm;
import com.example.demoDMS1.Model.RegistrationForm;
import com.example.demoDMS1.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<?> registerEmployee(@RequestBody RegistrationForm registrationForm){
        System.out.println(registrationForm);
        return employeeService.registerEmployee(registrationForm);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginEmployee(@RequestBody LoginForm loginForm){
        return employeeService.authenticateEmployee(loginForm);
    }
}
