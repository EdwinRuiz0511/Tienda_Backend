package com.tienda.backend.repository;

import com.tienda.backend.entity.DetalleFacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetalleFacturaRepository extends JpaRepository<DetalleFacturaEntity, Long> {

    @Query("SELECT COUNT(d) from DetalleFacturaEntity d WHERE d.productosEnt.id_Producto = :id_producto")
    long contarDetallesPorProducto(@Param("id_producto") Long id_Producto);
}
