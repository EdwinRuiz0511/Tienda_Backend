package com.tienda.backend.dto;

import com.tienda.backend.entity.DetalleFacturaEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductosDTO {

    private Long id_Productos;
    private int precio;
    private List<DetalleFacturaEntity> listaDetalleFacturaEnt;
}
