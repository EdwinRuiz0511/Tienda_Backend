package com.tienda.backend.repository;

import com.tienda.backend.entity.DetalleFacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDetalleFacturaRepository extends JpaRepository<DetalleFacturaEntity, Long> {

    //Realizar el CRUD de Detalle_Factura
}
