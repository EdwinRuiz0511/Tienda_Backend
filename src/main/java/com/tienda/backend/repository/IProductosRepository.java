package com.tienda.backend.repository;

import com.tienda.backend.entity.ProductosEntity;
import com.tienda.backend.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProductosRepository extends JpaRepository<ProductosEntity, Long> {

    Optional<ProductosEntity> findByNombreProducto(String nombreProducto);
}
