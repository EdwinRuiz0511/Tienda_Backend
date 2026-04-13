package com.tienda.backend.service;

import com.tienda.backend.dto.ProductosDTO;
import org.springframework.web.multipart.MultipartFile;

public interface IProductoService {

    void guardarCsv(MultipartFile file);

    ProductosDTO agregarProducto(ProductosDTO productosDto);
}
