package com.tienda.backend.service.impl;

import com.tienda.backend.dto.CargaCsvDTO;
import com.tienda.backend.entity.CargaCsvEntity;
import com.tienda.backend.entity.EstadoCarga;
import com.tienda.backend.entity.UsuarioEntity;
import com.tienda.backend.exception.RecursoNoEncontradoException;
import com.tienda.backend.mapper.ICargaCsvMapper;
import com.tienda.backend.repository.ICargaCsvRepository;
import com.tienda.backend.service.ICargaCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CargaCsvServiceImpl implements ICargaCsvService {

    @Autowired
    private ICargaCsvRepository iCargaCsvRepository;

    @Autowired
    private ICargaCsvMapper iCargaCsvMapper;

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public Long crearCargaPendiente(String nombreArchivo, UsuarioEntity usuarioEnt) {
        CargaCsvEntity cargaCsvEnt = new CargaCsvEntity();
        cargaCsvEnt.setNombreArchivo(nombreArchivo);
        cargaCsvEnt.setEstado(EstadoCarga.PENDIENTE);
        cargaCsvEnt.setFechaInicio(LocalDateTime.now());
        cargaCsvEnt.setUsuarioEnt(usuarioEnt);

        CargaCsvEntity guardada = iCargaCsvRepository.save(cargaCsvEnt);

        return guardada.getId_Carga();
    }

    @Override
    @Transactional
    public void marcarComoExitosa(Long idCarga, Integer filasOriginales, Integer productosGuardados) {
        CargaCsvEntity cargaCsvEnt = iCargaCsvRepository.findById(idCarga)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carga con ID --> "+idCarga+" No encontrada"));

        cargaCsvEnt.setEstado(EstadoCarga.EXITOSO);
        cargaCsvEnt.setFilasOriginales(filasOriginales);
        cargaCsvEnt.setProductosGuardados(productosGuardados);
        cargaCsvEnt.setFechaFin(LocalDateTime.now());

        iCargaCsvRepository.save(cargaCsvEnt);
    }

    @Override
    @Transactional
    public void marcarComoError(Long idCarga, String mensajeError) {
        CargaCsvEntity cargaCsvEnt = iCargaCsvRepository.findById(idCarga)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carga con ID --> "+idCarga+" No encontrada"));

        cargaCsvEnt.setEstado(EstadoCarga.ERROR);
        cargaCsvEnt.setMensajeError(mensajeError);
        cargaCsvEnt.setFechaFin(LocalDateTime.now());

        iCargaCsvRepository.save(cargaCsvEnt);
    }

    @Override
    public CargaCsvDTO consultarEstado(Long idCarga) {
        CargaCsvEntity cargaCsvEnt = iCargaCsvRepository.findById(idCarga)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carga con ID --> "+idCarga+" No encontrada"));

        return iCargaCsvMapper.toDTO(cargaCsvEnt);
    }
}
