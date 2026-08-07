package com.tienda.backend.controller;

import com.tienda.backend.dto.FacturaDTO;
import com.tienda.backend.service.IFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    IFacturaService facturaService;

    @PostMapping("/agregarFactura")
    public ResponseEntity<FacturaDTO> agregarFactura(@RequestBody FacturaDTO facturaDto) {
        FacturaDTO agregarFactura = facturaService.agregarFactura(facturaDto);
        return ResponseEntity.ok(agregarFactura);
    }

    @GetMapping("/listarFacturas")
    public ResponseEntity<List<FacturaDTO>> listarFcaturas() {
        List<FacturaDTO> facturaDto = facturaService.listarFacturasDto();
        return ResponseEntity.ok(facturaDto);
    }

    @PutMapping("/actualizarFactura/{id_Factura}")
    public ResponseEntity<FacturaDTO> actualizarFactura (@PathVariable Integer id_Factura, @RequestBody FacturaDTO facturaDto) {
        FacturaDTO facturaActualizada  = facturaService.actuzalizarFactura(id_Factura, facturaDto);
        return ResponseEntity.ok(facturaActualizada);
    }

    @DeleteMapping("/eliminar/{id_Factura}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Integer id_Factura) {
        facturaService.eliminarFactura(id_Factura);
        return ResponseEntity.noContent().build();
    }
}
