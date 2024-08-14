package com.app.SpringSecurityJaax.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    @GetMapping("/sayHelloPublic")
    public String sayHello(){
        return "Hello from API";
    }

    @GetMapping("sayHelloProtected")
    public String sayHelloProtected(){
        return "Hello from API Protected";
    }

}
