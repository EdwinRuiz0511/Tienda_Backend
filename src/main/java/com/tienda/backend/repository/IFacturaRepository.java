package com.tienda.backend.repository;

import com.tienda.backend.entity.FacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacturaRepository extends JpaRepository <FacturaEntity, Integer> {

}
