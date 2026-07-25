package com.tienda.backend.mapper;

import com.tienda.backend.dto.DetalleFacturaDTO;
import com.tienda.backend.entity.DetalleFacturaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

// Este mapper es el encargado de convertir la información relacionada con los detalles de una factura.
@Mapper(componentModel = "spring")
public interface IDetalleFacturaMapper {

    @Mapping(target = "id_Factura", source = "facturaEnt.id_Factura")
    @Mapping(target = "id_Producto",     source = "productosEnt.id_Producto")
    @Mapping(target = "nombreProducto",  source = "productosEnt.nombreProducto")
    @Mapping(target = "categoria",       source = "productosEnt.categoria")
    @Mapping(target = "precio",          source = "productosEnt.precio")
    DetalleFacturaDTO toDTOBasico(DetalleFacturaEntity detalleFacturaEnt);


    // Convierte un conjunto de detalles de factura.
    // Se mantiene como parte de la API pública del mapper para que pueda
    // reutilizarse desde otros servicios o mappers cuando sea necesario.
    Set<DetalleFacturaDTO> toDTOSet(Set<DetalleFacturaEntity> detalleFacturaEntities);
}
