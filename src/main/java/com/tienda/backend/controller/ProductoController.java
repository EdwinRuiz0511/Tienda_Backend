package com.tienda.backend.controller;

import com.tienda.backend.dto.ProductosDTO;
import com.tienda.backend.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private IProductoService productoService;

    @PostMapping("/upload")
    public ResponseEntity<String> subirCsv(@RequestParam("file") MultipartFile file) {
        productoService.guardarCsv(file);
        return ResponseEntity.ok("CSV cargado correctamente ✅");
    }

    @PostMapping("/agregarProducto")
    public ResponseEntity<ProductosDTO> agregarProducto(@RequestBody ProductosDTO productosDto) {
        ProductosDTO respuesta = productoService.agregarProducto(productosDto);
        return ResponseEntity.ok(respuesta);
    }
}
