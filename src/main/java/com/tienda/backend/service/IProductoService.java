package com.tienda.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface IProductoService {

    void guardarCsv(MultipartFile file);
}
