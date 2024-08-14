package com.app.SpringSecurityJaax.controller;

import com.app.SpringSecurityJaax.controller.dto.AuthReponse;
import com.app.SpringSecurityJaax.controller.dto.AuthenticateRequest;
import com.app.SpringSecurityJaax.controller.dto.RegisterRequest;
import com.app.SpringSecurityJaax.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthReponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthReponse> authenticate(@RequestBody AuthenticateRequest request){
        return ResponseEntity.ok(authService.authenticate(request));
    }


}
