package com.Oauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(http ->{
                    http.requestMatchers(HttpMethod.GET,"/","/hello").permitAll();
                    http.anyRequest().authenticated();
                })
                .formLogin(Customizer.withDefaults())
                //Spring security maneja por defecto 4 proveedores externos authentication: Github, Google, Facebook y Octa
                //La sesion mediante oauth2 se guarda en una cookie
                .oauth2Login(Customizer.withDefaults())
                .build();
    }
}
