package com.example.demoDMS1.ServiceImpl;

import com.example.demoDMS1.Entity.UserEntity;
import com.example.demoDMS1.Model.LoginForm;
import com.example.demoDMS1.Model.RegistrationForm;
import com.example.demoDMS1.Repository.UserRepository;
import com.example.demoDMS1.Service.UserRoleService;
import com.example.demoDMS1.Service.UserService;
import com.example.demoDMS1.Utility.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

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
            return ResponseEntity.badRequest().body(ResponseBuilder.failedRegistrationResponse("User already exists"));
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
            return ResponseEntity.ok().body(ResponseBuilder.successFullRegistrationResponse(true,"User: "+ registrationForm.getUserEmail() + " registered successfully"));
        }
    }

    @Override
    public ResponseEntity<?> authenticateUser(LoginForm loginForm) {
        boolean userExists = checkIfUserExists(loginForm.getUserEmail());

        if (userExists) {
            String encodedPassword = userRepository.findEncodedPasswordByEmail(loginForm.getUserEmail());
            boolean doesPasswordMatch = passwordEncoder.matches(loginForm.getUserPassword(), encodedPassword);
            boolean doesUserAuthorityLevelMatch = Objects.equals(userRepository.findUserAuthorityLevelByUserEmail(loginForm.getUserEmail()), userRoleService.getUserAuthorityLevel(loginForm.getUserRole()));

            if (doesPasswordMatch && doesUserAuthorityLevelMatch) {
                return ResponseEntity.ok().body(ResponseBuilder.successfulLoginResponse(true, loginForm.getUserEmail(), loginForm.getUserRole(),"User successfully logged in"));
            } else {
                return ResponseEntity.badRequest().body(ResponseBuilder.failedLoginResponse(false,"Either password or Role doesn't match"));
            }
        }
        return ResponseEntity.badRequest().body(ResponseBuilder.failedLoginResponse(false,"Email does not exist"));
    }

    @Override
    public boolean checkIfUserExists(String userEmail) {
        return userRepository.existsByUserEmail(userEmail);
    }
}
