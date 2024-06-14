package com.example.demoDMS1.Controller;

import com.example.demoDMS1.Model.LoginForm;
import com.example.demoDMS1.Model.RegistrationForm;
import com.example.demoDMS1.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationForm registrationForm){
        System.out.println(registrationForm);
        return userService.registerUser(registrationForm);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginForm loginForm){
        return userService.authenticateUser(loginForm);
    }
}
