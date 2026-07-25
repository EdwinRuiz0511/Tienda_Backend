package com.tienda.backend.mapper;

import com.tienda.backend.dto.FacturaDTO;
import com.tienda.backend.entity.FacturaEntity;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;

// uses = {IDetalleFacturaMapper.class} le indica a MapStruct que cuando
// necesite convertir Set<DetalleFacturaEntity> → Set<DetalleFacturaDTO>
// (campo listaDetallesFacturas dentro de FacturaEntity), debe usar el método
// toDTOSetCompleto() que ya existe en IDetalleFacturaMapper, en lugar de generar
// código duplicado para la misma conversión.
@Mapper(componentModel = "spring", uses = {IDetalleFacturaMapper.class})
public interface IFacturaMapper {

    // Convierte únicamente los datos básicos de la factura. (sin incluir sus detalles).
    @Named("facturaSinDetalles")
    @Mapping(target = "id_Usuario", source = "usuarioEnt.id_Usuario")
    @Mapping(target = "listaDetallesFacturas", ignore = true)
    FacturaDTO toDTOSinDetalles(FacturaEntity facturaEnt);

    // Convierte la factura con toda su información relacionada. (incluyendo sus detalles).
    @Named("facturaConDetalles")
    @Mapping(target = "id_Usuario", source = "usuarioEnt.id_Usuario")
    FacturaDTO toDTOConDetalles(FacturaEntity facturaEnt);

    // Convierte un conjunto de facturas con toda su información.
    @IterableMapping(qualifiedByName = "facturaConDetalles")
    Set<FacturaDTO> toDTOSetCompleto(Set<FacturaEntity> facturaEntities);

    // Convierte un conjunto de facturas con información básica.
    @IterableMapping(qualifiedByName = "facturaSinDetalles")
    Set<FacturaDTO> toDTOSetBasico(Set<FacturaEntity> facturaEntities);
}
