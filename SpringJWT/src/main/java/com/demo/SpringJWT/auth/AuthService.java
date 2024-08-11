package com.demo.SpringJWT.auth;

import com.demo.SpringJWT.auth.dto.AuthResponseDTO;
import com.demo.SpringJWT.auth.dto.LoginRequestDTO;
import com.demo.SpringJWT.auth.dto.RegisterRequestDTO;
import com.demo.SpringJWT.jwt.JwtService;
import com.demo.SpringJWT.user.Role;
import com.demo.SpringJWT.user.User;
import com.demo.SpringJWT.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    /*En esta parte del código se añade como atributos PasswordEncoder y AuthenticationManager, elemento que configuramos antes en
    * el SecurityConfig, me queda la duda de porque a pesar de configurarlos debemos traerlos como atributos, entonces si es así porque es
    * necesario su configuración */
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String token = jwtService.getToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        User user = User.builder().username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .country(request.getCountry())
                .role(Role.USER)// En este ejemplo se añade solo usuarios con rol user
                .build();

        userRepository.save(user);

        return AuthResponseDTO.builder()
                .token(jwtService.getToken(user))
                .build();
    }
}
