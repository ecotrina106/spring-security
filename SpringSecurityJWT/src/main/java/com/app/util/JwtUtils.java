package com.app.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

//Componente de utilidad para uso de tokens
@Component
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    // TENER EN CUENTA QUE ESTAMOS USANDO LA LIBRERIA jwt-java, revisar documentacion: https://github.com/auth0/java-jwt, la implementacion
    // puede variar en otras librerias (documentación usada del 19/07/2024, puede haber variado)

    //Usamos Authentication de spring security para obtener los usuarios con sus permisos
    //En el Authentication el Principal es el que tiene al usuario, y Authorities serian los permisos
    public String createToken(Authentication authentication){
        Algorithm algorithm = Algorithm.HMAC256(this.privateKey);

        String username = authentication.getPrincipal().toString();

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String JWTToken = JWT.create()
                .withIssuer(this.userGenerator)  // Usuario generador de token, No estoy seguro si estrictamente necesario, validar
                .withSubject(username)   // Usuario al que se crea el token
                .withClaim("authorities",authorities)  //Uso de claims para poner permisos
                .withIssuedAt(new Date())    //Fecha en la que se generó el token
                .withExpiresAt(new Date(System.currentTimeMillis()+1800000)) //Tiempo de expiración
                .withJWTId(UUID.randomUUID().toString())   //Id unico del token agregado
                .withNotBefore(new Date(System.currentTimeMillis()))   //Tiempo a partir del que token es válido
                .sign(algorithm);   //Firma con el algoritmo, devuelve en string

        return JWTToken;
    }

    public DecodedJWT validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(this.privateKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(userGenerator)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        }catch (JWTVerificationException exception){
            throw new JWTVerificationException("Token invalido, no autorizado");
        }

    }

    public String extractUserName(DecodedJWT decodedJWT){
        return decodedJWT.getSubject();
    }
    public String getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName).toString();
    }
    public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }

}
