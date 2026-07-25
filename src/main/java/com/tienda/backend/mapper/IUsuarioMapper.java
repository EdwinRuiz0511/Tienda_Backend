package com.tienda.backend.mapper;


import com.tienda.backend.dto.UsuarioDTO;
import com.tienda.backend.entity.UsuarioEntity;
import org.mapstruct.*;

import java.util.List;

// uses = {IFacturaMapper.class} le indica a MapStruct que cuando necesite
// convertir Set<FacturaEntity> → Set<FacturaDTO> (campo listaDeFacturas),
// debe usar IFacturaMapper. A su vez, IFacturaMapper ya usa IDetalleFacturaMapper,
// así que toda la cadena de conversión queda cubierta automáticamente.

// unmappedTargetPolicy = ReportingPolicy.WARN:
// Muestra una advertencia si existe algún campo del destino que no fue mapeado,
// ayudando a detectar posibles omisiones durante el desarrollo.
@Mapper(componentModel = "spring", uses = {IFacturaMapper.class}, unmappedTargetPolicy = ReportingPolicy.WARN)
public interface IUsuarioMapper {

    // Convierte únicamente los datos básicos del usuario.
    @Named("usuarioBasico")
    @Mapping(target = "listaDeFacturas", ignore = true)
    @Mapping(target = "nombreRol", source = "rolEnt.nombreRol")
    UsuarioDTO toDTOBasico(UsuarioEntity usuarioEnt);

    // nombreRol necesita @Mapping porque hay que atravesar rolEnt para llegar a él.
    // El resto de campos (id_Usuario, nombre, apellido, sexo, telefono, username,
    // password, listaDeFacturas) MapStruct los conecta automáticamente por nombre.

    // Convierte el usuario junto con sus facturas (sin incluir sus detalles).
    // Usado por: listar_Usuarios_Factu_PorId
    @Named("usuarioConFacturas")
    @Mapping(target = "listaDeFacturas", source = "listaDeFacturas", qualifiedByName = "facturaSinDetalles")
    @Mapping(target = "nombreRol",source = "rolEnt.nombreRol")
    UsuarioDTO toDTOConFacturas(UsuarioEntity usuarioEnT);

    // Convierte Usuario CON facturas Y detalles --> Usuario + Facturas + Detalles
    // Usado por: listar_Usuarios_Factu_Detall_PorId
    @Named("usuarioCompleto")
    @Mapping(target = "listaDeFacturas", source = "listaDeFacturas", qualifiedByName = "facturaConDetalles")
    @Mapping(target = "nombreRol", source = "rolEnt.nombreRol")
    UsuarioDTO toDTOCompleto(UsuarioEntity usuarioEnt);

    // Ignora id_Usuario (autoincremental) y campos de seguridad que nunca
    // deben modificarse desde un DTO de actualización
    // Actualiza únicamente los campos editables del usuario.
    @Mapping(target = "id_Usuario", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rolEnt", ignore = true)
    @Mapping(target = "listaDeFacturas", ignore = true)
    void actualizarUsuario(UsuarioDTO usuarioDto, @MappingTarget UsuarioEntity usuarioEnt);

    // Convierte una lista de usuarios con información básica.
    @IterableMapping(qualifiedByName = "usuarioBasico")
    List<UsuarioDTO> toDTOListBasico(List<UsuarioEntity> usuarioEntities);

    // Convierte una lista de usuarios junto con sus facturas.
    @IterableMapping(qualifiedByName = "usuarioConFacturas")
    List<UsuarioDTO> toDTOListFacturas(List<UsuarioEntity> usuarioEntities);

}
