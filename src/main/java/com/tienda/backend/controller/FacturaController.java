package com.tienda.backend.controller;

import com.tienda.backend.dto.FacturaDTO;
import com.tienda.backend.service.IFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    IFacturaService facturaService;

    @PostMapping("/agregarFactura")
    public ResponseEntity<?> agregarFactura(@RequestBody FacturaDTO facturaDto) {
        try {
            FacturaDTO agregarFactura = facturaService.agregarFactura(facturaDto);
            return ResponseEntity.ok().body(agregarFactura);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: "+e.getMessage());
        }
    }

    @GetMapping("/listarFacturas")
    public ResponseEntity<List<FacturaDTO>> listarFcaturas() {
        List<FacturaDTO> facturaDto = facturaService.listarFacturasDto();
        return ResponseEntity.ok(facturaDto);
    }

    @PutMapping("/actualizarFactura/{idFactura}")
    public ResponseEntity<?> actualizarFactura (@PathVariable Integer idFactura, @RequestBody FacturaDTO facturaDto) {
        try {
            FacturaDTO facturaActualizada = facturaService.actuzalizarFactura(idFactura, facturaDto);
            return ResponseEntity.ok(facturaActualizada);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: "+e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{idFactura}")
    public ResponseEntity<String> eliminarFactura(@PathVariable Integer idFactura) {
        try {
            facturaService.eliminarFactura(idFactura);
            return  ResponseEntity.noContent().build();

        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: "+e.getMessage());
        }
    }
}
