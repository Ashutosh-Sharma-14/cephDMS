package com.example.demoDMS1.Service;

import com.example.demoDMS1.Model.AuthenticationRequest;
import com.example.demoDMS1.Model.AuthenticationResponse;
import com.example.demoDMS1.Model.RegisterRequest;
import com.example.demoDMS1.Model.RegistrationResponse;
import com.example.demoDMS1.Entity.Role;
import com.example.demoDMS1.Entity.User;
import com.example.demoDMS1.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final AuthenticationManager authenticationManager;
    public RegistrationResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .userRole(registerRequest.getUserRole())
                .userEmail(registerRequest.getUserEmail())
                .userPassword(passwordEncoder.encode(registerRequest.getUserPassword()))
                .userAuthorityLevel(registerRequest.getUserAuthorityLevel())
                .role(Role.USER)
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return RegistrationResponse.builder()
                .token(jwtToken)
                .isRegistered(true)
                .message("User registered successfully")
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUserEmail(),
                            authenticationRequest.getUserPassword())
            );
        }
        catch (AuthenticationException e){
            return AuthenticationResponse.builder()
                    .isLoggedIn(false)
                    .message("Invalid Credentials")
                    .build();
        }
        var user = userRepository.findByUserEmail(authenticationRequest.getUserEmail());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .isLoggedIn(true)
                .userEmail(user.getUserEmail())
                .userRole(user.getUserRole())
                .message("User: " + user.getUserEmail() + " logged in successfully")
                .build();
    }
}
