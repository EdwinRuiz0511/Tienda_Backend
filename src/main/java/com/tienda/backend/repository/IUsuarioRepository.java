package com.tienda.backend.repository;

import com.tienda.backend.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepository extends JpaRepository <UsuarioEntity, Long> {

}
