package com.tienda.backend.controller;

import com.tienda.backend.dto.DetalleFacturaDTO;
import com.tienda.backend.service.IDetalleFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/detalle-factura")
public class DetalleFacturaController {

    @Autowired
    IDetalleFacturaService detalleFacturaService;

    //  Crear detalle de factura
    @PostMapping("/crear")
    public DetalleFacturaDTO crearDetalle(@RequestBody DetalleFacturaDTO dto) {
        return detalleFacturaService.agregarDetalleFactura(dto);
    }

}
