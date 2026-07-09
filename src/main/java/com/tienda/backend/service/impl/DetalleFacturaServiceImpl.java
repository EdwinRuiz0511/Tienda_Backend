package com.tienda.backend.service.impl;

import com.tienda.backend.dto.DetalleFacturaDTO;
import com.tienda.backend.entity.DetalleFacturaEntity;
import com.tienda.backend.entity.FacturaEntity;
import com.tienda.backend.entity.ProductosEntity;
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



    // Frontend → DTO → Entity → Base de datos
    private DetalleFacturaEntity dtoToEntit(DetalleFacturaDTO detalleFacturaDto) {
        DetalleFacturaEntity detalleFacturaEnt = new DetalleFacturaEntity();
        detalleFacturaEnt.setCantProductos(detalleFacturaDto.getCantProductos());
        detalleFacturaEnt.setTotal(detalleFacturaDto.getTotal());

        return detalleFacturaEnt;
    }

    // Base de datos → Entity → DTO → frontend
    private DetalleFacturaDTO entityToDto(DetalleFacturaEntity detalleFacturaEnt) {
        DetalleFacturaDTO detalleFacturaDto = new DetalleFacturaDTO();
        detalleFacturaDto.setId_DetalleFac(detalleFacturaEnt.getId_DetalleFac());
        detalleFacturaDto.setCantProductos(detalleFacturaEnt.getCantProductos());
        detalleFacturaDto.setTotal(detalleFacturaEnt.getTotal());

        return detalleFacturaDto;
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public DetalleFacturaDTO agregarDetalleFactura(DetalleFacturaDTO detalleFacturaDto) {

        // Buscar factura
        FacturaEntity facturaEnt = facturaRepository.findById(detalleFacturaDto.getId_Factura()).
                orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        // Buscar Producto
        ProductosEntity productosEnt = productosRepository.findById(detalleFacturaDto.getId_Producto()).
                orElseThrow(() -> new RuntimeException("Producto no encontrado"));


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

        facturaEnt.setTotalFactura(nuevoTotal);
        facturaRepository.save(facturaEnt);

        // Pasar de Entity -> DTO
        DetalleFacturaDTO respuesta = new DetalleFacturaDTO();
        respuesta.setId_DetalleFac(guardado.getId_DetalleFac());
        respuesta.setNombreProducto(productosEnt.getNombreProducto());
        respuesta.setPrecio(productosEnt.getPrecio());
        respuesta.setCantProductos(guardado.getCantProductos());
        respuesta.setTotal(guardado.getTotal());
        respuesta.setId_Producto(productosEnt.getId_Producto());
        respuesta.setId_Factura(facturaEnt.getId_Factura());

        return respuesta;
    }

    @Override
    @Transactional                                                                                                      // 🔒 Si algo falla, se cancela TODO lo que se estaba guardando
    public List<DetalleFacturaDTO> agregarListaDeTallesFactura(List<DetalleFacturaDTO> listaDetallesFactura) {

        // 1. Validar que la lista no venga vacia o nula
        if (listaDetallesFactura == null || listaDetallesFactura.isEmpty()) {
            throw new RuntimeException("Debe enviar al menos un Detalle de Factura");
        }

        // 2. Obtener el id de la factura del primer elemento, se asume que todos los detalles pertenecen a la misma factura
        Integer facturaId = listaDetallesFactura.get(0).getId_Factura();

        // Verificamos que TODOS los detalles enviados correspondan a la misma factura.
        boolean mismaFactura = listaDetallesFactura.stream()
                .allMatch(detalle -> detalle.getId_Factura().equals(facturaId));

        if (!mismaFactura) {
            throw new RuntimeException("Todos los detalles deben pertenecer a la misma factura");
        }

        // 3. Buscar la factura en la BD
        FacturaEntity facturaEnt = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

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
        for(DetalleFacturaDTO detalleFacturaDTO : listaDetallesFactura) {
            // Buscar producto en el mapa -> O(1)
            ProductosEntity productosEntity = mapaProductosEnt.get(detalleFacturaDTO.getId_Producto());

            // Si no existe el producto → error
            if (productosEntity == null) {
                throw new RuntimeException("Producto no encontrado ID: " + detalleFacturaDTO.getId_Producto());
            }

            // Crear entidad detalle
            DetalleFacturaEntity detalleFacturaEnt = new DetalleFacturaEntity();
            detalleFacturaEnt.setCantProductos(detalleFacturaDTO.getCantProductos());
            detalleFacturaEnt.setFacturaEnt(facturaEnt);
            detalleFacturaEnt.setProductosEnt(productosEntity);

            // Calcular subtotal
            Float subtotal  = (float) (detalleFacturaDTO.getCantProductos() * productosEntity.getPrecio());
            detalleFacturaEnt.setTotal(subtotal );

            // Agregar a lista de guardado
            detallesGuardar.add(detalleFacturaEnt);

            // Sumar al total acumulado
            totalFactura += subtotal;
        }

        // 10. Guardar TODOS los detalles en una sola operación
        detalleFacturaRepository.saveAll(detallesGuardar);

        // 11. Actualizar el total en la tabla de BD Factura
        facturaEnt.setTotalFactura(totalFactura);
        facturaRepository.save(facturaEnt);


        // 12. Convertir respuesta entity -> DTO
        return detallesGuardar.stream().map(detalleEnt -> {
            DetalleFacturaDTO detalleFacturaDTO = new DetalleFacturaDTO();
            detalleFacturaDTO.setId_DetalleFac(detalleEnt.getId_DetalleFac());
            detalleFacturaDTO.setNombreProducto(detalleEnt.getProductosEnt().getNombreProducto());
            detalleFacturaDTO.setPrecio(detalleEnt.getProductosEnt().getPrecio());
            detalleFacturaDTO.setCantProductos(detalleEnt.getCantProductos());
            detalleFacturaDTO.setTotal(detalleEnt.getTotal());
            detalleFacturaDTO.setId_Producto(detalleEnt.getProductosEnt().getId_Producto());
            detalleFacturaDTO.setId_Factura(detalleEnt.getFacturaEnt().getId_Factura());
            return detalleFacturaDTO;
        }).collect(Collectors.toList());
    }
}


/*

METODO agregarListaDeTallesFactura REFACTORIZADO, P

    // ==============================
    // MÉTODO PRINCIPAL (ORQUESTADOR)
    // ==============================
    @Override
    @Transactional
    public List<DetalleFacturaDTO> agregarListaDeTallesFactura(List<DetalleFacturaDTO> listaDetallesFactura) {

        validarLista(listaDetallesFactura);

        FacturaEntity factura = obtenerFactura(listaDetallesFactura);

        Map<Long, ProductosEntity> mapaProductos = obtenerMapaProductos(listaDetallesFactura);

        List<DetalleFacturaEntity> detalles = crearDetalles(listaDetallesFactura, factura, mapaProductos);

        actualizarTotalFactura(factura, detalles);

        return convertirADTO(detalles);
    }


    // ==============================
    // VALIDACIÓN
    // ==============================
    private void validarLista(List<DetalleFacturaDTO> lista) {
        if (lista == null || lista.isEmpty()) {
            throw new RuntimeException("Debe enviar al menos un Detalle de Factura");
        }
    }


    // ==============================
    // FACTURA
    // ==============================
    private FacturaEntity obtenerFactura(List<DetalleFacturaDTO> lista) {

        Integer facturaId = lista.get(0).getId_Factura();

        return facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));
    }


    // ==============================
    // PRODUCTOS
    // ==============================
    private Map<Long, ProductosEntity> obtenerMapaProductos(List<DetalleFacturaDTO> lista) {

        List<Long> ids = lista.stream()
                .map(DetalleFacturaDTO::getId_Producto)
                .distinct()
                .toList();

        List<ProductosEntity> productos = productosRepository.findAllById(ids);

        return productos.stream()
                .collect(Collectors.toMap(ProductosEntity::getId_Productos, p -> p));
    }


    // ==============================
    // CREAR DETALLES
    // ==============================
    private List<DetalleFacturaEntity> crearDetalles(
            List<DetalleFacturaDTO> lista,
            FacturaEntity factura,
            Map<Long, ProductosEntity> mapaProductos) {

        List<DetalleFacturaEntity> detalles = new ArrayList<>();

        for (DetalleFacturaDTO dto : lista) {

            ProductosEntity producto = mapaProductos.get(dto.getId_Producto());

            if (producto == null) {
                throw new RuntimeException("Producto no encontrado ID: " + dto.getId_Producto());
            }

            DetalleFacturaEntity detalle = new DetalleFacturaEntity();
            detalle.setCantProductos(dto.getCantProductos());
            detalle.setFacturaEnt(factura);
            detalle.setProductosEnt(producto);

            float subtotal = dto.getCantProductos() * producto.getPrecio();
            detalle.setTotal(subtotal);

            detalles.add(detalle);
        }

        return detalleFacturaRepository.saveAll(detalles);
    }


    // ==============================
    // ACTUALIZAR FACTURA
    // ==============================
    private void actualizarTotalFactura(FacturaEntity factura, List<DetalleFacturaEntity> detalles) {

        float totalActual = Optional.ofNullable(factura.getTotalFactura()).orElse(0f);

        float totalNuevo = detalles.stream()
                .map(DetalleFacturaEntity::getTotal)
                .reduce(0f, Float::sum);

        factura.setTotalFactura(totalActual + totalNuevo);

        facturaRepository.save(factura);
    }


    // ==============================
    // CONVERTIR A DTO
    // ==============================
    private List<DetalleFacturaDTO> convertirADTO(List<DetalleFacturaEntity> detalles) {

        return detalles.stream().map(det -> {
            DetalleFacturaDTO dto = new DetalleFacturaDTO();
            dto.setId_DetalleFac(det.getId_DetalleFac());
            dto.setCantProductos(det.getCantProductos());
            dto.setTotal(det.getTotal());
            dto.setId_Producto(det.getProductosEnt().getId_Productos());
            dto.setId_Factura(det.getFacturaEnt().getId_Factura());
            return dto;
        }).toList();
    }
}

// 6. Convertir la lista de productos obtenida de la BD en un Map para búsquedas rápidas por ID
Map<Long, ProductosEntity> mapaProductosEnt = productosEnt.stream()     // Convierte la lista de productos en un stream para procesarla funcionalmente

        // Construye un Map a partir del stream
        .collect(Collectors.toMap(

                ProductosEntity :: getId_Productos,   // 🔑 CLAVE del Map → será el ID del producto
                                                      // Es equivalente a: p -> p.getId_Productos()

                p -> p                                // 📦 VALOR del Map → será el objeto producto completo
                                                      // No se transforma, se guarda tal cual

        ));


 */