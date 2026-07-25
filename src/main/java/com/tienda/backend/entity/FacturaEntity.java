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
@Table(name = "factura")
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Factura")
    private int id_Factura;

    @Column(name = "totalFactura")
    private Float totalFactura = 0f;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_Usuario") //FK
    private UsuarioEntity usuarioEnt;

    @OneToMany(mappedBy = "facturaEnt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<DetalleFacturaEntity> listaDetallesFacturas = new HashSet<>();

}
