package com.app.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
//Anotacion habilitada por spring security al poner EnableMethodSecurity en el SecurityConfig
@PreAuthorize("denyAll()")
public class TestAuthController {

    @GetMapping("/hello")
    //Permite el acceso sin estar authorizado, público
    @PreAuthorize("permitAll()")
    public String hello(){
        return "Hello world";
    }

    @GetMapping("/hello-secured")
    //Permite el acceso al estar authorizado con permiso
    @PreAuthorize("hasAuthority('CREATE')")
    public String helloSecured(){
        return "Hello world Secured";
    }
    @GetMapping("/hello-secured2")
    @PreAuthorize("hasAuthority('READ')")
    public String helloSecured2(){
        return "Hello world Secured 2";
    }
}
