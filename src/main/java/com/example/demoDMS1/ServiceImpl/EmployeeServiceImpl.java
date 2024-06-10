package com.example.demoDMS1.ServiceImpl;

import com.example.demoDMS1.Entity.EmployeeEntity;
import com.example.demoDMS1.Model.LoginForm;
import com.example.demoDMS1.Model.RegistrationForm;
import com.example.demoDMS1.Repository.EmployeeRepository;
import com.example.demoDMS1.Service.EmployeeService;
import org.apache.coyote.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeServiceImpl implements EmployeeService {

//    injecting bean of class which extends to JpaRepository
    @Autowired
    EmployeeRepository employeeRepository;

//    injecting bean of PasswordEncoder class which is defined in SecurityConfig
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> registerEmployee(RegistrationForm registrationForm) {
        EmployeeEntity employeeEntity = new EmployeeEntity();

//        encoding the password
        String encodedPassword = passwordEncoder.encode(registrationForm.getEmployeePassword());
        registrationForm.setEmployeePassword(encodedPassword);

        if(!checkIfEmployeeExists(registrationForm.getEmployeeEmail())){
            BeanUtils.copyProperties(registrationForm,employeeEntity);
            employeeRepository.save(employeeEntity);
            return ResponseEntity.ok().body(true);
        }
        else{
            return ResponseEntity.badRequest().body("Email already exists");
        }
    }

    @Override
    public ResponseEntity<?> authenticateEmployee(LoginForm loginForm) {
        if(employeeRepository.existsByEmployeeEmail(loginForm.getEmployeeEmail())){
            String encodedPassword = employeeRepository.findEncodedPasswordByEmail(loginForm.getEmployeeEmail());
            boolean doesEmployeeRoleMatch = loginForm.getEmployeeRole().equals(employeeRepository.findEmployeeRoleByEmployeeEmail(loginForm.getEmployeeEmail()));
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
        return employeeRepository.existsByEmployeeEmail(employeeEmail);
    }
}
