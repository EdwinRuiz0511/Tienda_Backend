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
@Table(name = "productos")
public class ProductosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Productos")
    private Long id_Productos;

    @Column(name = "nombreProducto")
    private String nombreProducto;

    @Column(name = "precio")
    private int precio;

    @OneToMany(mappedBy = "productosEnt", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<DetalleFacturaEntity> listaDetalleFacturaEnt = new ArrayList<>();
}
