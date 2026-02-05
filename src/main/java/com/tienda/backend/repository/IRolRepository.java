package com.tienda.backend.repository;

import com.tienda.backend.entity.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRolRepository extends JpaRepository<RolEntity, Long> {

    Optional<RolEntity> findByNombreRol(String nombreRol);
}
