package com.app.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
//Anotacion habilitada por spring security al poner EnableMethodSecurity en el SecurityConfig
@PreAuthorize("denyAll()")
public class TestAuthController {

//    @GetMapping("/hello")
//    //Permite el acceso sin estar authorizado, público
//    @PreAuthorize("permitAll()")
//    public String hello(){
//        return "Hello world";
//    }
//
//    @GetMapping("/hello-secured")
//    //Permite el acceso al estar authorizado con permiso
//    @PreAuthorize("hasAuthority('CREATE')")
//    public String helloSecured(){
//        return "Hello world Secured";
//    }
//    @GetMapping("/hello-secured2")
//    @PreAuthorize("hasAuthority('READ')")
//    public String helloSecured2(){
//        return "Hello world Secured 2";
//    }


    @GetMapping("/get")
    @PreAuthorize("has('READ')")
    public String helloGet(){
        return "Hello World - GET";
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('CREATE')")
    public String helloPost(){
        return "Hello World - POST";
    }

    @PutMapping("/put")
    public String helloPut(){
        return "Hello World - PUT";
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloDelete(){
        return "Hello World - DELETE";
    }

    @PatchMapping("/patch")
    @PreAuthorize("hasAuthority('REFACTOR')")
    public String helloPatch(){
        return "Hello World - PATCH";
    }
}
