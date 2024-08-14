package com.app.SpringSecurityJaax.service;

import com.app.SpringSecurityJaax.controller.dto.AuthReponse;
import com.app.SpringSecurityJaax.controller.dto.AuthenticateRequest;
import com.app.SpringSecurityJaax.controller.dto.RegisterRequest;
import com.app.SpringSecurityJaax.entity.Role;
import com.app.SpringSecurityJaax.entity.User;
import com.app.SpringSecurityJaax.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthReponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) // en este ejemplo solo registra con rol USER
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthReponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthReponse authenticate(AuthenticateRequest request) {

        //Authenticando el jwt
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findUserByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthReponse.builder()
                .token(jwtToken).build();
    }
}
