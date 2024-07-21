package com.app.service;

import com.app.controller.dto.AuthCreateUserRequest;
import com.app.controller.dto.AuthLoginRequest;
import com.app.controller.dto.AuthResponse;
import com.app.persistence.entity.RoleEntity;
import com.app.persistence.entity.UserEntity;
import com.app.persistence.repository.RoleRepository;
import com.app.persistence.repository.UserRepository;
import com.app.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


//Se crea userDetailService personalizado para obtener usuarios de BD, se requiere implementar UserDetailsService de spring security
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //Obteniendo userEntity de Base de datos
        UserEntity userEntity = userRepository
                .findUserEntityByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario no existe"));

        //Mapeando a User de Spring Security (clase que implementa interfaz UserDetails)

        //Mapeando permisos (authorities) en SimpleGrantedAuthority, clase que implementa interfaz GrantedAuthority, tipo de dato que necesita
        // el User de spring security private final Set<GrantedAuthority> authorities;
        List<SimpleGrantedAuthority> authorityList = getAuthoritiesOfUserEntity(userEntity);

        //Se retorna y arma el objeto User de spring security
        return new User(userEntity.getUsername(), userEntity.getPassword(),
                userEntity.isEnabled(),userEntity.isAccountNoExpired(),userEntity.isCredentialNoExpired(),
                userEntity.isAccountNoLocked(),authorityList);
    }

    public AuthResponse loginUser(AuthLoginRequest authLoginRequest){
        String userName = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(userName,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.createToken(authentication);

        return new AuthResponse(userName,"Usuario logueado exitosamente",token,true);

    }

    private Authentication authenticate(String user, String pass){
        UserDetails userDetails = this.loadUserByUsername(user);
        if(userDetails == null){
            throw new BadCredentialsException("Usuario o contraseña invalidos");
        }

        if(!passwordEncoder.matches(pass,userDetails.getPassword())){
            throw new BadCredentialsException("Usuario o contraseña invalidos");
        }

        return new UsernamePasswordAuthenticationToken(user,userDetails.getPassword(),userDetails.getAuthorities());
    }

    public AuthResponse createUser(AuthCreateUserRequest authCreateUserRequest){
        String username = authCreateUserRequest.username();
        String pass = authCreateUserRequest.password();
        List<String> rolesRequest = authCreateUserRequest.roleRequest().roleListName();

        // Validar los roles (que esten en BD)
        Set<RoleEntity> roleEntitySet = new HashSet<>(roleRepository.findRoleEntitiesByRoleEnumIn(rolesRequest));

        //Si no coincide con un rol se tira excepcion
        if(roleEntitySet.isEmpty()){
            throw new IllegalArgumentException("Roles especificados no existen");
        }

        UserEntity userEntity = UserEntity
                .builder()
                .username(username)
                .password(passwordEncoder.encode(pass))
                .roles(roleEntitySet)
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();

        UserEntity userCreated = userRepository.save(userEntity);

        List<SimpleGrantedAuthority> authorityList = getAuthoritiesOfUserEntity(userCreated);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(),userCreated.getPassword(),authorityList);
        String token = jwtUtils.createToken(authentication);
        return new AuthResponse(userCreated.getUsername(),"Usuario creado exitosamente",token,true);
    }


    private List<SimpleGrantedAuthority> getAuthoritiesOfUserEntity(UserEntity userEntity){
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userEntity.getRoles().forEach(role-> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        userEntity.getRoles().stream()
                .flatMap(role->role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));

        return authorityList;

    }
}
