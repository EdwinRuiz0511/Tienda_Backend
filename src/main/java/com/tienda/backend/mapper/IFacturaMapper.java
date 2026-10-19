package com.tienda.backend.mapper;

import com.tienda.backend.dto.FacturaDTO;
import com.tienda.backend.entity.FacturaEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

// uses = {IDetalleFacturaMapper.class} le indica a MapStruct que cuando
// necesite convertir Set<DetalleFacturaEntity> → Set<DetalleFacturaDTO>
// (campo listaDetallesFacturas dentro de FacturaEntity), debe usar el método
// toDTOSetCompleto() que ya existe en IDetalleFacturaMapper, en lugar de generar
// código duplicado para la misma conversión.
@Mapper(componentModel = "spring", uses = {IDetalleFacturaMapper.class})
public interface IFacturaMapper {

    // Dto -> Entity, ignoramos id_Factura porque es autoincremental, y usuarioEnt para id_Usuario ya que lo manejamos desde el service
    // y totalFactura porque es un valor calculado por el sistema (se actualiza con iFacturaRepository.actualizarTotal al agregar detalles), nunca debe venir del cliente
    @Mapping(target = "id_Factura", ignore = true)
    @Mapping(target = "usuarioEnt", ignore = true)
    @Mapping(target = "totalFactura", ignore = true)
    @Mapping(target = "listaDetallesFacturas", ignore = true)
    FacturaEntity toEntity(FacturaDTO facturaDto);

    @Named("facturaSinDetallesNiUsuario")
    @Mapping(target = "id_Usuario", source = "usuarioEnt.id_Usuario")
    @Mapping(target = "nombre", ignore = true)
    @Mapping(target = "apellido", ignore = true)
    @Mapping(target = "telefono", ignore = true)
    @Mapping(target = "listaDetallesFacturas", ignore = true)
    FacturaDTO toDTOBasico(FacturaEntity facturaEnt);

    // Convierte únicamente los datos básicos de la factura. (sin incluir sus detalles, pero si los datos del usuario, lo usamos en agregarFactura).
    @Named("facturaSinDetalles")
    @Mapping(target = "id_Usuario", source = "usuarioEnt.id_Usuario")
    @Mapping(target = "nombre", source = "usuarioEnt.nombre")
    @Mapping(target = "apellido", source = "usuarioEnt.apellido")
    @Mapping(target = "telefono", source = "usuarioEnt.telefono")
    @Mapping(target = "listaDetallesFacturas", ignore = true)
    FacturaDTO toDTOBasicoConUsuario(FacturaEntity facturaEnt);

    // Convierte la factura con toda su información relacionada. (incluyendo sus detalles).
    @Named("facturaConDetalles")
    @Mapping(target = "id_Usuario", source = "usuarioEnt.id_Usuario")
    @Mapping(target = "nombre", ignore = true)
    @Mapping(target = "apellido", ignore = true)
    @Mapping(target = "telefono", ignore = true)
    FacturaDTO toDTOConDetalles(FacturaEntity facturaEnt);

    // Ignora los campos que no deben modificarse. Actualiza únicamente los datos editables de la factura.
    @Mapping(target = "id_Factura", ignore = true)
    @Mapping(target = "totalFactura", ignore = true)
    @Mapping(target = "listaDetallesFacturas", ignore = true)
    @Mapping(target = "usuarioEnt", ignore = true)
    void actualizarFactura(FacturaDTO facturaDto, @MappingTarget FacturaEntity facturaEnt);

    // Convierte una lista de facturas con información básica.
    @IterableMapping(qualifiedByName = "facturaSinDetalles")
    List<FacturaDTO> toDTOListBasico(List<FacturaEntity> facturaEntities);

    // Convierte un conjunto de facturas con toda su información.
    @IterableMapping(qualifiedByName = "facturaConDetalles")
    Set<FacturaDTO> toDTOSetCompleto(Set<FacturaEntity> facturaEntities);

    // Convierte un conjunto de facturas con información básica.
    @IterableMapping(qualifiedByName = "facturaSinDetallesNiUsuario")
    Set<FacturaDTO> toDTOSetBasico(Set<FacturaEntity> facturaEntities);
}
