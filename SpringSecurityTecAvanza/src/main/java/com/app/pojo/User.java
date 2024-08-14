package com.app.pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


//Hace esta línea para uso de jdbc también - investigar
@NamedQuery(name = "User.findByEmail", query = " select u from User u where u.email=:email")

@Data
@Entity
//Agrega los dynamics de lombok para poder hacer queries dinamicas al momento de insertar o actualizar sin datos que sean nulos
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String numeroDeContacto;
    private String email;
    private String password;
    private String status;
    private String role;

}
