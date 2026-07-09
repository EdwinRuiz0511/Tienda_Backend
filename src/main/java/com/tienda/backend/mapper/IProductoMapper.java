package com.tienda.backend.mapper;

import com.tienda.backend.dto.ProductosDTO;
import com.tienda.backend.entity.ProductosEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IProductoMapper {

    ProductosDTO toDTO(ProductosEntity productosEnt);

    // Ignora el id al mapear DTO → Entity porque es autoincremental
    @Mapping(target = "id_Producto", ignore = true)
    ProductosEntity toEntity(ProductosDTO productosDto);

    List<ProductosDTO> toDTOList(List<ProductosEntity> productosEntities);

    @Mapping(target = "id_Producto", ignore = true)
    void actualizarProducto(ProductosDTO productosDto, @MappingTarget ProductosEntity productosEnt);  // @MappingTarget → actualiza un objeto existente → no retorna nada (void).
}
