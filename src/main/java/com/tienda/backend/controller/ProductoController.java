package com.tienda.backend.controller;

import com.tienda.backend.dto.ProductosDTO;
import com.tienda.backend.repository.IUsuarioRepository;
import com.tienda.backend.service.ICargaCsvService;
import com.tienda.backend.service.IProductoService;
import jakarta.validation.Valid;
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
    private IProductoService iProductoService;

    @Autowired
    private ICargaCsvService cargaCsvService;

    @Autowired
    private IUsuarioRepository iUsuarioRepository;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> subirCsv(@RequestParam("archivoCsv") MultipartFile file) {
        Long idCarga = iProductoService.iniciarCargaCsv(file);
        return ResponseEntity.ok(Map.of("idSeguimiento", idCarga, "mensaje", "..."));
    }

    @PostMapping("/agregarProducto")
    public ResponseEntity<ProductosDTO> agregarProducto(@Valid @RequestBody ProductosDTO producto) {
        ProductosDTO productoDto = iProductoService.agregarProducto(producto);
        return ResponseEntity.ok(productoDto);
    }

    @GetMapping("/listarProductos")
    public ResponseEntity<List<ProductosDTO>> listarProductos() {
        List<ProductosDTO> listarProductos = iProductoService.listarProductos();
        return ResponseEntity.ok(listarProductos);
    }

    @PutMapping("/actualizarProducto/{id_Productos}")
    public ResponseEntity<ProductosDTO> actualizarProducto(@PathVariable Long id_Productos, @Valid @RequestBody ProductosDTO productosDto) {
        ProductosDTO productoActualizado = iProductoService.actualizarProducto(id_Productos, productosDto);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/eliminarProducto/{id_Productos}")
    public ResponseEntity<Void> eliminarProducto (@PathVariable Long id_Productos) {
        iProductoService.eliminarProducto(id_Productos);
       return ResponseEntity.noContent().build();
    }
}
