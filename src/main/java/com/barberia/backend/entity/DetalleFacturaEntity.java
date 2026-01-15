package com.barberia.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "detalle_factura")
public class DetalleFacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_DetalleFactura")
    private Long id_DetalleFac;

    @Column(name = "cant_Productos")
    private int cantProductos;

    @Column(name = "total")
    private float total;

    @ManyToOne
    @JoinColumn(name = "id_Factura") //FK
    private FacturaEntity facturaEnt;

    @ManyToOne
    @JoinColumn(name = "id_Productos") //Fk
    private ProductosEntity productosEnt;


    //Esta es la que lidera porque es de muchos a uno
}
