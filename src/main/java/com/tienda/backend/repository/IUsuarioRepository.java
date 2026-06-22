package com.tienda.backend.repository;

import com.tienda.backend.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository <UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByUsername(String username);

    // Trae usuario + facturas en una sola consulta
    @Query("SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.listaFacturaEnt WHERE u.id_Usuario = :id")
    Optional<UsuarioEntity> findByIdConFacturas(@Param("id") Long id);

    // Trae usuario + facturas + detalles en una sola consulta
    @Query("SELECT DISTINCT u FROM UsuarioEntity u " +
            "LEFT JOIN FETCH u.listaFacturaEnt f " +
            "LEFT JOIN FETCH f.listaDetalleFacturaEnt d " +
            "LEFT JOIN FETCH d.productosEnt " +
            "WHERE u.id_Usuario = :id")
    Optional<UsuarioEntity> findByIdConFacturasYDetalles(@Param("id") Long id);
}
