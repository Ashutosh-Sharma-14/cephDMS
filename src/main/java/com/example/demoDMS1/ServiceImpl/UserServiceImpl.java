package com.example.demoDMS1.ServiceImpl;

import com.example.demoDMS1.Entity.UserEntity;
import com.example.demoDMS1.Model.LoginForm;
import com.example.demoDMS1.Model.RegistrationForm;
import com.example.demoDMS1.Repository.UserRepository;
import com.example.demoDMS1.Service.UserRoleService;
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
    private UserRepository userRepository;

    @Autowired
    private UserRoleService userRoleService;

    // Injecting bean of PasswordEncoder class which is defined in SecurityConfig
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> registerUser(RegistrationForm registrationForm) {
        if(checkIfUserExists(registrationForm.getUserEmail())){
            return ResponseEntity.badRequest().body("Email already exists");
        }
        else{
            UserEntity userEntity = new UserEntity();

            // Encoding the password
            String encodedPassword = passwordEncoder.encode(registrationForm.getUserPassword());
            registrationForm.setUserPassword(encodedPassword);

//            BeanUtils.copyProperties(registrationForm, userEntity);
            userEntity.setUserEmail(registrationForm.getUserEmail());
            userEntity.setUserPassword(registrationForm.getUserPassword());
            userEntity.setUserRole(registrationForm.getUserRole());
            userEntity.setUserAuthorityLevel(userRoleService.getUserAuthorityLevel(registrationForm.getUserRole()));

            userRepository.save(userEntity);
            return ResponseEntity.ok().body(true);
        }
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginForm loginForm) {
        if (userRepository.existsByUserEmail(loginForm.getUserEmail())) {
            String encodedPassword = userRepository.findEncodedPasswordByEmail(loginForm.getUserEmail());
            boolean doesUserRoleMatch = loginForm.getUserRole().equals(userRepository.findUserRoleByUserEmail(loginForm.getUserEmail()));
            boolean doesPasswordMatch = passwordEncoder.matches(loginForm.getUserPassword(), encodedPassword);

            if (doesPasswordMatch && doesUserRoleMatch) {
                return ResponseEntity.ok().body("Logged in");
            } else {
                return ResponseEntity.badRequest().body("Either role or password doesn't match");
            }
        }
        return ResponseEntity.badRequest().body("Email does not exist");
    }

    @Override
    public boolean checkIfUserExists(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }
}
