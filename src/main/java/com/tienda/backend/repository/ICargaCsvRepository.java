package com.tienda.backend.repository;

import com.tienda.backend.entity.CargaCsvEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICargaCsvRepository extends JpaRepository <CargaCsvEntity, Long> {
}
