package com.app.config;

import com.app.service.UserDetailServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

/*
@Configuration -> Para informar a spring que es una clase de configuracion
@EnableWebSecurity -> Habilita la configuración de seguridad web en Spring Security.
@EnableMethodSecurity -> Habilita la seguridad a nivel de métodos en Spring Security. Como @PreAuthorize, @PostAuthorize, @Secured, y @RolesAllowed
 */

/*
Las clases anotadas con @Configuration pueden definir beans mediante métodos anotados con @Bean.
*/
/*
Cuando se utiliza @EnableWebSecurity, se pueden crear configuraciones personalizadas extendiendo la clase WebSecurityConfigurerAdapter
o implementando la interfaz SecurityConfigurer. Además, permite definir reglas de seguridad, como la autenticación y autorización de
solicitudes HTTP.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    //SecurityFilterChain -> cadena de filtros del spring security,es como un conjunto de middlewares de validación
    //httpSecurity -> objeto que recorre todos los filtros de la cadena de filtro(chain filter)
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .csrf(csrf -> csrf.disable()) //Vulnerabildiad explotada en formularios
//                //Usado para authentication basica de user y pass
//                .httpBasic(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Sesion sin estado, no manejamos la sesion internamente, si no con tokens por ejemplo
//                //Definicion de permisos de APIS
//                .authorizeHttpRequests(http -> {
//                    //PUBLICSO - Permite a todos ver la API, salvo que se haga un basic auth erroneo
//                    http.requestMatchers(HttpMethod.GET,"/auth/hello").permitAll();
//                    //PRIVADOS - Permite a todos los que tienen permiso CREATE
//                    http.requestMatchers(HttpMethod.GET,"/auth/hello-secured").hasAuthority("CREATE");
//
//                    //NO ESPECIFICADO
//                    http.anyRequest().authenticated();
//                })
//                .build();
//    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity
//                .csrf(csrf -> csrf.disable())
//                .httpBasic(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(http -> {
//                    // Configurar los endpoints publicos
//                    http.requestMatchers(HttpMethod.GET, "/auth/get").permitAll();
//
//                    // Configurar los endpoints privados
//                    http.requestMatchers(HttpMethod.POST, "/auth/post").hasAnyRole("ADMIN", "DEVELOPER");
//                    http.requestMatchers(HttpMethod.PATCH, "/auth/patch").hasAnyAuthority("REFACTOR");
//
//                    // Configurar el resto de endpoint - NO ESPECIFICADOS
//                    http.anyRequest().denyAll();
//                })
//                .build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) //Vulnerabildiad explotada en formularios
                //Usado para authentication basica de user y pass
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Sesion sin estado, no manejamos la sesion internamente, si no con tokens por ejemplo
                .build();
    }

    //AuthenticationManager necesita ser definido por el AuthenticationConfiguration de spring security
    //AuthenticationManager es el que gestiona las peticiones de authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //============================== Parte Inicial donde no se tenia BD
    //AuthenticationProvider -> es una interfaz que tiene muchos proveedores de auth implementandolo
    //en este caso se usará DaoAuthenticationProvider, para obtener los usuariosd e la BD
    /*DaoAuthenticationProvider -> para esta arquitectura planteada, ver imagen de readme.md del repo general
        PasswordEncoder - es el que se necesita para revisión de encriptacion de la contraseña para añadrilo a BD
        UserDetailsService - servicio especificado para obtener info de los usarios de BD. OJO que se debe usar la clase de
                             Spring Security definida con ese nombre "UserDetailsService", no confundir con alguno que podamos crear nosotros
    */
//    @Bean
//    public AuthenticationProvider authenticationProvider(){
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(userDetailsService());
//        return provider;
//    }
    //==============================

    //AuthenticationProvider -> es una interfaz que tiene muchos proveedores de auth implementandolo
    //en este caso se usará DaoAuthenticationProvider, para obtener los usuariosd e la BD
    /*DaoAuthenticationProvider -> para esta arquitectura planteada, ver imagen de readme.md del repo general
        PasswordEncoder - es el que se necesita para revisión de encriptacion de la contraseña para añadrilo a BD
        UserDetailsService - servicio especificado para obtener info de los usarios de BD. OJO que se debe usar la clase de
                             Spring Security definida con ese nombre "UserDetailsService", no confundir con alguno que podamos crear nosotros

      Se usa en este caso el UserDetailServiceImpl que es un servicio definido por nosotros que implementa la funcion para mapear el User de la bd
      a un user de spring security
    */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    //PasswordEncoder -> utilizado para el DaoAuthenticationProvider para revision de pass
    //NoOpPasswordEncoder -> por ahora utilizado apra aprender, SOLO DEBE SER USADO EN PRUEB, no en producción, OJO esta deprecated.
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return NoOpPasswordEncoder.getInstance();
//    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //===================================USADO CUANDO NO SE TENIA BD, solo explicativo
    //UserDetailsService -> Interfaz de Spring Security para manejar la data de usuarios provenientes de BD
    //Además se tiene un UserDetails y User del mismo Spring Security, NO CONFUNDIR con nuestro dominio

    //En este caso no consultamos a BD si no que definimos un User para retornarlo dentro de un UserDetailsService con
    //InMemoryUserDetailsManager que implementa UserDetailsService
    @Bean
    public UserDetailsService userDetailsService(){
        List<UserDetails> userDetailsList = new ArrayList<>();
        userDetailsList.add(User.withUsername("messi")
                .password("1234").roles("ADMIN").authorities("READ","CREATE")
                .build());
        userDetailsList.add(User.withUsername("cristiano")
                .password("1234").roles("ADMIN").authorities("READ")
                .build());
        return new InMemoryUserDetailsManager(userDetailsList);
    }
}
