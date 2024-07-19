package com.app.config.filter;


import com.app.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

//OncePerRequestFilter, clase abstracta que va a ejecutar el filtro por cada request
public class JWTTokenValidator extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    public JWTTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //Obtenemos token del header del request Authorization
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(jwtToken!=null) { //token es -> Bearer 9sdf34s..
            jwtToken = jwtToken.substring(7);
            //validamos token
             DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
             //extraemos userName
             String userName = jwtUtils.extractUserName(decodedJWT);
             //obtenemos authorities en un string separado por comas, esto es por como lo hemos programado
             String authoritiesString = jwtUtils.getSpecificClaim(decodedJWT,"authorities");

             //Se usa AuthorityUtils de spring security que devuelve un collection de Authorities a partir del string separado por comas
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesString);

            //Se obtiene el contexto, y se inserta la authentication en el SecurityContextHolder
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = new UsernamePasswordAuthenticationToken(userName,null,authorities);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

        }

        //Si el token es null continua la cada de filtros y fallará por defecto
        filterChain.doFilter(request,response);
    }
}
