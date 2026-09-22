package com.tienda.backend.mapper;

import com.tienda.backend.dto.CargaCsvDTO;
import com.tienda.backend.entity.CargaCsvEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ICargaCsvMapper {

    @Mapping(target = "nombreUsuario", source = "usuarioEnt.nombre")
    @Mapping(target = "id_Usuario", source = "usuarioEnt.id_Usuario")
    CargaCsvDTO toDTO(CargaCsvEntity cargaCsvEnt);
}
