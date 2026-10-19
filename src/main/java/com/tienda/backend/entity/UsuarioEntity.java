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

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "sexo", nullable = false)
    private String sexo;

    @Column(name = "telefono", nullable = false)
    private String telefono;

    //Campos para Login
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    // RelacionesFacturaDTO

    // Relacion con la tabala FacturaEntity
    @OneToMany(mappedBy = "usuarioEnt", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<FacturaEntity> listaDeFacturas = new HashSet<>();

    //Relacion con la tabla RolEntity
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_Rol")
    private RolEntity rolEnt;
}

