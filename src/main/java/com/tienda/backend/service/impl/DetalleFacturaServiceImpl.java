package com.tienda.backend.service.impl;

import com.tienda.backend.dto.DetalleFacturaDTO;
import com.tienda.backend.entity.DetalleFacturaEntity;
import com.tienda.backend.entity.FacturaEntity;
import com.tienda.backend.entity.ProductosEntity;
import com.tienda.backend.exception.RecursoNoEncontradoException;
import com.tienda.backend.exception.SolicitudInvalidaException;
import com.tienda.backend.mapper.IDetalleFacturaMapper;
import com.tienda.backend.repository.IDetalleFacturaRepository;
import com.tienda.backend.repository.IFacturaRepository;
import com.tienda.backend.repository.IProductosRepository;
import com.tienda.backend.service.IDetalleFacturaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DetalleFacturaServiceImpl implements IDetalleFacturaService {

    @Autowired
    private IDetalleFacturaRepository detalleFacturaRepository;

    @Autowired
    private IFacturaRepository facturaRepository;

    @Autowired
    private IProductosRepository productosRepository;

    @Autowired
    IDetalleFacturaMapper iDetalleFacturaMapper;

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    @Transactional
    public DetalleFacturaDTO agregarDetalleFactura(DetalleFacturaDTO detalleFacturaDto) {

        // Buscar factura
        FacturaEntity facturaEnt = facturaRepository.findById(detalleFacturaDto.getId_Factura()).
                orElseThrow(() -> new RecursoNoEncontradoException("Factura con ID --> °"+detalleFacturaDto.getId_Factura()+"° No encontrado"));

        // Buscar Producto
        ProductosEntity productosEnt = productosRepository.findById(detalleFacturaDto.getId_Producto()).
                orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID --> °"+detalleFacturaDto.getId_Producto()+"° NO encontrado"));


        // Crear Entidad de DetallesFactura DTO → Entity:
        DetalleFacturaEntity detalleFacturaEnt = new DetalleFacturaEntity();
        detalleFacturaEnt.setCantProductos(detalleFacturaDto.getCantProductos());
        detalleFacturaEnt.setFacturaEnt(facturaEnt);
        detalleFacturaEnt.setProductosEnt(productosEnt);

        //Calcular Total
        float total = (float) (detalleFacturaDto.getCantProductos() * productosEnt.getPrecio());
        detalleFacturaEnt.setTotal(total);

        // Guardar en BD (DetalleFcatura)
        DetalleFacturaEntity guardado = detalleFacturaRepository.save(detalleFacturaEnt);

        // Actualizar factura tambien y acumular
        Float totalActual = facturaEnt.getTotalFactura();
        if (totalActual == null) {
            totalActual = 0f;
        }
        float nuevoTotal = totalActual + total;

        facturaRepository.actualizarTotal(facturaEnt.getId_Factura(), nuevoTotal);

        // Pasar de Entity -> DTO
        DetalleFacturaDTO respuesta = iDetalleFacturaMapper.toDTOBasico(guardado);

        return respuesta;
    }

    @Override
    @Transactional                                                                                                      // 🔒 Si algo falla, se cancela TODO lo que se estaba guardando
    public List<DetalleFacturaDTO> agregarListaDeTallesFactura(List<DetalleFacturaDTO> listaDetallesFactura) {

        // 1. Validar que la lista no venga vacia o nula
        if (listaDetallesFactura == null || listaDetallesFactura.isEmpty()) {
            throw new SolicitudInvalidaException("Debe enviar al menos un Detalle de Factura");
        }

        // 2. Obtener el id de la factura del primer elemento, se asume que todos los detalles pertenecen a la misma factura
        Integer facturaId = listaDetallesFactura.get(0).getId_Factura();

        // Verificamos que TODOS los detalles enviados correspondan a la misma factura.
        boolean mismaFactura = listaDetallesFactura.stream()
                .allMatch(detalle -> detalle.getId_Factura().equals(facturaId));

        if (!mismaFactura) {
            throw new SolicitudInvalidaException("Todos los detalles deben pertenecer a la misma factura");
        }

        // 3. Buscar la factura en la BD
        FacturaEntity facturaEnt = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Factura con ID --> °"+facturaId+"° No encontrada"));

        // 4. Obtener lista de IDs de productos (sin repetir) -> O(n)
                List<Long> idsProductos = listaDetallesFactura.stream()                                                 // Recorre la lista
                .map(DetalleFacturaDTO :: getId_Producto)                                                               // extrae solo los IDs
                .distinct()                                                                                             // elimina los repetidos
                .collect(Collectors.toList());                                                                          // devuelve la lista final

        // 5. Traer todos los productos en UNA sola consulta a BD
        List<ProductosEntity> productosEnt = productosRepository.findAllById(idsProductos);

        // 6. Convertir lista de productos en mapa para búsqueda rápida -> O(n)
        Map<Long, ProductosEntity> mapaProductosEnt = productosEnt.stream()
                .collect(Collectors.toMap(ProductosEntity :: getId_Producto, p -> p));

        // 7. Lista donde guardaremos los detalles listos para insertar
        List<DetalleFacturaEntity> detallesGuardar = new ArrayList<>();

        // 8. Obtener total actual de la factura (si es null → 0)
        float totalFactura = Optional.ofNullable(facturaEnt.getTotalFactura()).orElse(0f);

        // 9. Recorrer cada detalle enviado desde el cliente
        for(DetalleFacturaDTO detalleFacturaDto : listaDetallesFactura) {
            // Buscar producto en el mapa -> O(1)
            ProductosEntity productosEntity = mapaProductosEnt.get(detalleFacturaDto.getId_Producto());

            // Si no existe el producto → error
            if (productosEntity == null) {
                throw new RecursoNoEncontradoException("Producto con ID --> °"+detalleFacturaDto.getId_Producto()+"° NO encontrado");
            }

            // Crear entidad detalle
            DetalleFacturaEntity detalleFacturaEnt = new DetalleFacturaEntity();
            detalleFacturaEnt.setCantProductos(detalleFacturaDto.getCantProductos());
            detalleFacturaEnt.setFacturaEnt(facturaEnt);
            detalleFacturaEnt.setProductosEnt(productosEntity);

            // Calcular subtotal
            Float subtotal  = (float) (detalleFacturaDto.getCantProductos() * productosEntity.getPrecio());
            detalleFacturaEnt.setTotal(subtotal );

            // Agregar a lista de guardado
            detallesGuardar.add(detalleFacturaEnt);

            // Sumar al total acumulado
            totalFactura += subtotal;
        }

        // 10. Guardar TODOS los detalles en una sola operación
        detalleFacturaRepository.saveAll(detallesGuardar);

        // 11. Actualizar el total en la tabla de BD Factura
        //facturaEnt.setTotalFactura(totalFactura);
        facturaRepository.actualizarTotal(facturaId, totalFactura);

        // 12. Convertir respuesta entity -> DTO
        return detallesGuardar
                .stream()
                .map(iDetalleFacturaMapper :: toDTOBasico)
                .collect(Collectors.toList());
    }
}