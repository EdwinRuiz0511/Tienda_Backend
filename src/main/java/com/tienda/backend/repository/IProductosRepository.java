package com.tienda.backend.repository;

import com.tienda.backend.entity.ProductosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductosRepository extends JpaRepository<ProductosEntity, Long> {
}
