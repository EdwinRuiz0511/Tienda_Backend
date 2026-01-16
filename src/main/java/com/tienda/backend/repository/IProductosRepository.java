package com.tienda.backend.repository;

import com.tienda.backend.entity.ProductosEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductosRepository extends JpaRepository<ProductosEntity, Long> {
}
