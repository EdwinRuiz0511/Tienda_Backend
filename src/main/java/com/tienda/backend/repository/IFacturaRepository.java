package com.tienda.backend.repository;

import com.tienda.backend.entity.FacturaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFacturaRepository extends JpaRepository <FacturaEntity, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE FacturaEntity f SET f.totalFactura = :total WHERE f.id_Factura = :id")
    void actualizarTotal(@Param("id") Integer id, @Param("total") Float total);

    @Query("SELECT f FROM FacturaEntity f JOIN FETCH f.usuarioEnt")
    List<FacturaEntity> findAllConUsuario();

}
