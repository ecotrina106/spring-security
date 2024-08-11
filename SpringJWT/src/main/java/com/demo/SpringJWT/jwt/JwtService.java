package com.demo.SpringJWT.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "79375YGOOHWFH349JF934JF034J9J340FJ20FJK34JF93J40KC3ZAXL9SALXCL430DL0342DL";

    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(Map<String, Object> extraClaims, UserDetails user) {
        return Jwts.builder().setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getKey(), SignatureAlgorithm.HS256) // firma del token con la key y el algoritmo
                .compact(); 
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); //pasar un secretKey en bytes
        return Keys.hmacShaKeyFor(keyBytes); //crear una nueva instancia del secretKey
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userToken = getUsernameFromToken(token);
        return (userToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //Método para obtener todos los claims del token
    private Claims getAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) //firmamos con la key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Método para obtener un claim en especifico brindando como parámetro la función del claim a obtener
    public <T> T getClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Date getExpirationDate(String token){
        return getClaim(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return getExpirationDate(token).before(new Date());
    }
}
