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
@Table(name = "productos")
public class ProductosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Productos")
    private Long id_Producto;

    @Column(name = "nombreProducto")
    private String nombreProducto;

    @Column(name = "precio")
    private double precio;

    @Column(name = "categoria")
    private String categoria;

    @OneToMany(mappedBy = "productosEnt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DetalleFacturaEntity> listaDetalleFacturaEnt = new HashSet<>();
}
