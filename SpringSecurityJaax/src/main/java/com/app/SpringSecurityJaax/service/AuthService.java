package com.app.SpringSecurityJaax.service;

import com.app.SpringSecurityJaax.controller.dto.AuthReponse;
import com.app.SpringSecurityJaax.controller.dto.AuthenticateRequest;
import com.app.SpringSecurityJaax.controller.dto.RegisterRequest;


public interface AuthService {
    AuthReponse register(RegisterRequest request);
    AuthReponse authenticate(AuthenticateRequest request);

}
