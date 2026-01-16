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
@Table(name = "factura")
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Factura")
    private int id_Factura;

    @Column(name = "totalFactura")
    private Float totalFactura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "identificacion_usuario")
    private UsuarioEntity usuarioEnt;

    @OneToMany(mappedBy = "facturaEnt", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<DetalleFacturaEntity> listaDetalleFacturaEnt = new ArrayList<>();

}

/*
@ManyToOne -> relación MUCHOS a UNO, Muchas Facturas pertenecen a UN Usuario
              AQUÍ vive la FK, Regla: La FK siempre está en el lado MANY


@JoinColumn(name = "identificacion_usuario") -> Nombre real de la columna en la tabla factura
                                                factura
                                                -------------------------
                                                id_factura
                                                total_factura
                                                identificacion_usuario  ← FK

                                                Apunta a: usuario.identificacion


fetch = FetchType.EAGER -> Cuando traes una factura, Hibernate trae también su usuario
*/
