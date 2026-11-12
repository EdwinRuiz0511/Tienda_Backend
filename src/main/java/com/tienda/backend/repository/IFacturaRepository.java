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

    /**
     * Actualiza directamente el total de una factura en la BD sin cargarla en memoria. @Modifying indica
     * que es un UPDATE; @Transactional asegura la transacción.Es más eficiente que un findById + set + save.
     */

    @Modifying
    @Transactional
    @Query("UPDATE FacturaEntity f SET f.totalFactura = :total WHERE f.id_Factura = :id")
    void actualizarTotal(@Param("id") Integer id, @Param("total") Float total);

    /**
     * Obtiene TODAS las facturas registradas en el sistema, pero trayendo de una sola vez
     * la información del usuario asociado a cada una.
     * El JOIN FETCH es clave aquí: evita el problema de las consultas N+1. Sin él,
     * al iterar las facturas en Java, Hibernate haría una consulta adicional a la BD
     * por cada factura solo para buscar su usuario.
     * Uso típico: Endpoint exclusivo para ADMIN que necesita ver el historial global de ventas.
     */

    @Query("SELECT f FROM FacturaEntity f JOIN FETCH f.usuarioEnt")
    List<FacturaEntity> findAllConUsuario();

    /**
     * Obtiene SOLO las facturas que pertenecen a un usuario específico, identificado por su 'username'.
     * También utiliza JOIN FETCH para traer los datos del usuario en la misma consulta (evitando N+1).
     *La diferencia con el método anterior es el WHERE: aquí filtramos por 'u.username = :username'.
     * (Se usa el alias 'u' asignado al JOIN FETCH para poder referenciar sus campos en el WHERE).
     *Uso típico: Endpoint para que un cliente (rol USER) consulte su propio historial de compras.
     */

    @Query("SELECT f FROM FacturaEntity f JOIN FETCH f.usuarioEnt u WHERE u.username = :username")
    List<FacturaEntity> findAllConUsuarioPorUsername(@Param("username") String username);

}
