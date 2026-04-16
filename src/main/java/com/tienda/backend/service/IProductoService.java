package com.tienda.backend.service;

import com.tienda.backend.dto.ProductosDTO;
import com.tienda.backend.entity.ProductosEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductoService {

    void guardarCsv(MultipartFile file);

    ProductosDTO agregarProducto(ProductosDTO productosDto);

    List<ProductosDTO> listarProductos();

    ProductosDTO actualizarProducto(Long id_Productos, ProductosDTO productosDto);

    void eliminarProducto(Long id_Productos);
}
