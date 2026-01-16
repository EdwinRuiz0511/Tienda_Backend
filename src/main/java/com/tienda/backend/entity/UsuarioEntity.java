package com.tienda.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificacion")
    private Long id_Usuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "sexo")
    private char sexo;

    @Column(name = "telefono")
    private int telefono;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "usuarioEnt")
    @JsonIgnore
    List<FacturaEntity> facturaEnt = new ArrayList<>();
}

/*
@OneToMany -> Un Usuario puede tener MUCHAS Facturas

cascade = CascadeType.ALL -> Lo que le pase al usuario, se replica en sus facturas

fetch = FetchType.EAGER ->

mappedBy = "usuarioEnt" -> La relación se controla desde la entidad FacturaEntity, específicamente
                           desde el atributo usuarioEnt. En otras palabras, UsuarioEntity NO tiene la FK, La FK está en FacturaEntity

List<FacturaEntity> facturaEnt = new ArrayList<>();
        Hay lista porque es 1 a muchos
        “Muchos” en Java = List
        La FK NO está en la lista
        La lista NO crea datos sola
        Sirve para navegar la relación
*/
