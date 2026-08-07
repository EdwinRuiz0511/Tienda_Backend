package com.tienda.backend.controller;

import com.tienda.backend.dto.ProductosDTO;
import com.tienda.backend.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private IProductoService productoService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> subirCsv(@RequestParam("file") MultipartFile file) {
        productoService.guardarCsv(file);
        return ResponseEntity.ok(Map.of("mensaje", "CSV cargado correctamente"));
    }

    @PostMapping("/agregarProducto")
    public ResponseEntity<ProductosDTO> agregarProducto(@RequestBody ProductosDTO producto) {
        ProductosDTO productoDto = productoService.agregarProducto(producto);
        return ResponseEntity.ok(productoDto);
    }

    @GetMapping("/listarProductos")
    public ResponseEntity<List<ProductosDTO>> listarProductos() {
        List<ProductosDTO> listarProductos = productoService.listarProductos();
        return ResponseEntity.ok(listarProductos);
    }

    @PutMapping("/actualizarProducto/{id_Productos}")
    public ResponseEntity<ProductosDTO> actualizarProducto(@PathVariable Long id_Productos, @RequestBody ProductosDTO productosDto) {
        ProductosDTO productoActualizado = productoService.actualizarProducto(id_Productos, productosDto);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/eliminarProducto/{id_Productos}")
    public ResponseEntity<Void> eliminarProducto (@PathVariable Long id_Productos) {
       productoService.eliminarProducto(id_Productos);
       return ResponseEntity.noContent().build();
    }
}
