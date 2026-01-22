package com.tienda.backend.service.impl;

import com.tienda.backend.dto.DetalleFacturaDTO;
import com.tienda.backend.entity.DetalleFacturaEntity;
import com.tienda.backend.entity.FacturaEntity;
import com.tienda.backend.entity.ProductosEntity;
import com.tienda.backend.repository.IDetalleFacturaRepository;
import com.tienda.backend.repository.IFacturaRepository;
import com.tienda.backend.repository.IProductosRepository;
import com.tienda.backend.service.IDetalleFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        respuesta.setCantProductos(guardado.getCantProductos());
        respuesta.setTotal(guardado.getTotal());
        respuesta.setId_Producto(productosEnt.getId_Productos());
        respuesta.setId_Factura(facturaEnt.getId_Factura());

        return respuesta;
    }
}
