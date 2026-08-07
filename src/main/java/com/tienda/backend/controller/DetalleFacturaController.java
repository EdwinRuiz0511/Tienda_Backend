package com.tienda.backend.controller;

import com.tienda.backend.dto.DetalleFacturaDTO;
import com.tienda.backend.service.IDetalleFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/detalle-factura")
public class DetalleFacturaController {

    @Autowired
    IDetalleFacturaService detalleFacturaService;

    //  Crear detalle de factura
    @PostMapping("/crear")
    public ResponseEntity<DetalleFacturaDTO> crearDetalles(@RequestBody DetalleFacturaDTO detalle) {
        DetalleFacturaDTO detalleFacturaDto = detalleFacturaService.agregarDetalleFactura(detalle);
        return ResponseEntity.ok(detalleFacturaDto);
    }

    @PostMapping("/crear-lista")
    public ResponseEntity<List<DetalleFacturaDTO>> crearListaDetalles(@RequestBody List<DetalleFacturaDTO> lista) {
        List<DetalleFacturaDTO> respuesta = detalleFacturaService.agregarListaDeTallesFactura(lista);
        return ResponseEntity.ok(respuesta);
    }
}
