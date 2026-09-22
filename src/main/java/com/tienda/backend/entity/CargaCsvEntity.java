package com.tienda.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carga_csv")
public class CargaCsvEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_CargaCsv")
    private Long id_Carga;

    @Column(name = "nombreArchivo")
    private String nombreArchivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoCarga estado;

    @Column(name = "filasOriginales")
    private Integer filasOriginales;

    @Column(name = "productosGuardados")
    private Integer productosGuardados;

    @Column(name = "mensajeError", length = 1000)
    private String mensajeError;

    @Column(name = "fechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin")
    private LocalDateTime fechaFin;

    // Relacion con la entidad de Usuario
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_Usuario")
    private UsuarioEntity usuarioEnt;


}
