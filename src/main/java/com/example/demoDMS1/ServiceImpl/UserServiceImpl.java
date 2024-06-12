package com.example.demoDMS1.ServiceImpl;

import com.example.demoDMS1.Entity.UserEntity;
import com.example.demoDMS1.Model.LoginForm;
import com.example.demoDMS1.Model.RegistrationForm;
import com.example.demoDMS1.Repository.UserRepository;
import com.example.demoDMS1.Service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserServiceImpl implements UserService {

//    injecting bean of class which extends to JpaRepository
    @Autowired
UserRepository userRepository;

//    injecting bean of PasswordEncoder class which is defined in SecurityConfig
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> registerEmployee(RegistrationForm registrationForm) {
        UserEntity employeeEntity = new UserEntity();

//        encoding the password
        String encodedPassword = passwordEncoder.encode(registrationForm.getEmployeePassword());
        registrationForm.setEmployeePassword(encodedPassword);

        if(!checkIfEmployeeExists(registrationForm.getEmployeeEmail())){
            BeanUtils.copyProperties(registrationForm,employeeEntity);
            userRepository.save(employeeEntity);
            return ResponseEntity.ok().body(true);
        }
        else{
            return ResponseEntity.badRequest().body("Email already exists");
        }
    }

    @Override
    public ResponseEntity<?> authenticateEmployee(LoginForm loginForm) {
        if(userRepository.existsByEmployeeEmail(loginForm.getEmployeeEmail())){
            String encodedPassword = userRepository.findEncodedPasswordByEmail(loginForm.getEmployeeEmail());
            boolean doesEmployeeRoleMatch = loginForm.getEmployeeRole().equals(userRepository.findEmployeeRoleByEmployeeEmail(loginForm.getEmployeeEmail()));
            boolean doesPasswordMatch = passwordEncoder.matches(loginForm.getEmployeePassword(),encodedPassword);

            if(doesPasswordMatch && doesEmployeeRoleMatch){
                return ResponseEntity.ok().body("Logged in");
            }
            else{
                return ResponseEntity.badRequest().body("Either role or password doesn't match");
            }
        }
        return ResponseEntity.badRequest().body("Email do not exist");
    }

    @Override
    public boolean checkIfEmployeeExists(String employeeEmail) {
        return userRepository.existsByEmployeeEmail(employeeEmail);
    }
}
