package com.tienda.backend.service.impl;

import com.tienda.backend.dto.FacturaDTO;
import com.tienda.backend.entity.FacturaEntity;
import com.tienda.backend.entity.UsuarioEntity;
import com.tienda.backend.exception.RecursoNoEncontradoException;
import com.tienda.backend.mapper.IFacturaMapper;
import com.tienda.backend.repository.IFacturaRepository;
import com.tienda.backend.repository.IUsuarioRepository;
import com.tienda.backend.service.IFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FacturaServiceImpl implements IFacturaService {

    @Autowired
    private IFacturaRepository facturaRepository;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    IFacturaMapper iFacturaMapper;

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public FacturaDTO agregarFactura(FacturaDTO facturaDto) {
        // 1. Buscar el usuario REAL desde la BD
        UsuarioEntity usuarioEnt = usuarioRepository.findById(facturaDto.getId_Usuario()).
                orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID °"+facturaDto.getId_Usuario()+"° No existe"));

        // 2. Crear la Factura, DTO → Entity
        FacturaEntity facturaEnt = iFacturaMapper.toEntity(facturaDto);

        // 3. Asignar el Usuario
        facturaEnt.setUsuarioEnt(usuarioEnt); //FK

        // 4. Guardar Factura
        facturaRepository.save(facturaEnt);

        // 5. Retornar DTO, Entity → DTO
        FacturaDTO respuesta = iFacturaMapper.toDTOSinDetalles(facturaEnt);

        return respuesta;
    }

    @Override
    public List<FacturaDTO> listarFacturasDto() {

        // Listamos todas las Facturas desde la BD
        List<FacturaEntity> facturasEnt = facturaRepository.findAllConUsuario();

        // DTO → FRONTEND
        return iFacturaMapper.toDTOListBasico(facturasEnt);
    }

    @Override
    @Transactional
    public FacturaDTO actuzalizarFactura(Integer id_Factura, FacturaDTO facturaDto) {

        // Se valida que la factura exista antes de actualizar.
        FacturaEntity facturaEnt = facturaRepository.findById(id_Factura).
                orElseThrow(() -> new RecursoNoEncontradoException("Factura con ID -> "+ id_Factura +" No encontrada"));

        // DTO → ENTITY (actualización)
        iFacturaMapper.actualizarFactura(facturaDto, facturaEnt);

        // ENTITY → BASE DE DATOS
        FacturaEntity facturaActualizada = facturaRepository.save(facturaEnt);

        // ENTITY → DTO
        return iFacturaMapper.toDTOSinDetalles(facturaActualizada);
    }

    @Override
    @Transactional
    public void eliminarFactura(Integer id_Factura) {

        // Se valida que la factura exista antes de eliminar.
        FacturaEntity facturaEnt = facturaRepository.findById(id_Factura).
                orElseThrow(() -> new RecursoNoEncontradoException("Factura con ID -> "+ id_Factura +" No encontrada"));

        // Se elimina la Factura.
        facturaRepository.delete(facturaEnt);

    }
}
