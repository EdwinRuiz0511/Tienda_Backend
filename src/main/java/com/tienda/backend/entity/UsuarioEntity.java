package com.tienda.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Usuario")
    private Long id_Usuario; //PK

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "sexo")
    private char sexo;

    @Column(name = "telefono")
    private String telefono;

    //Campos para Login
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // Relaciones

    // Relacion con la tabala FacturaEntity
    @OneToMany(mappedBy = "usuarioEnt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    Set<FacturaEntity> listaDeFacturas = new HashSet<>();

    //Relacion con la tabla RolEntity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_Rol")
    private RolEntity rolEnt;
}

