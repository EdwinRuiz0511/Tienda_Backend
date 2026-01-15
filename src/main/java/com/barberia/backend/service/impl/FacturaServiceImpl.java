package com.barberia.backend.service.impl;

import com.barberia.backend.dto.FacturaDTO;
import com.barberia.backend.entity.FacturaEntity;
import com.barberia.backend.entity.UsuarioEntity;
import com.barberia.backend.repository.IFacturaRepository;
import com.barberia.backend.repository.IUsuarioRepository;
import com.barberia.backend.service.IFacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FacturaServiceImpl implements IFacturaService {

    @Autowired
    private IFacturaRepository facturaRepository;
    @Autowired
    private IUsuarioRepository usuarioRepository;

    // Frontend → DTO → Entity → Base de datos
    private FacturaEntity dtoToEntity (FacturaDTO facturaDto){
        FacturaEntity facturaEnt = new FacturaEntity();              // No copia el ID porque aún no existe, en este punto el usuario no esta en la base de datos, la Entity esta solo en memoria
        facturaEnt.setTotalFactura(facturaDto.getTotalFactura());

        return facturaEnt;
    }

    // Base de datos → Entity → DTO → frontend
    private FacturaDTO entityToDto(FacturaEntity facturaEnt) {
        FacturaDTO facturaDto = new FacturaDTO();
        facturaDto.setIdfactura(facturaEnt.getId_Factura());
        facturaDto.setTotalFactura(facturaEnt.getTotalFactura()); //Ahora si se incluye la Identificacion por ya existe (la creó la BD)

        return facturaDto;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public FacturaDTO agregarFactura(FacturaDTO facturaDto) {
        // 1. Buscar el usuario REAL desde la BD
        UsuarioEntity usuarioEnt = usuarioRepository.findById(facturaDto.getUsuarioDto().getIdUsuario()).
                orElseThrow(() -> new RuntimeException("Usuario no existe"));

        // 2. Crear la Factura, DTO → Entity
        FacturaEntity facturaEnt = new FacturaEntity();
        facturaEnt.setTotalFactura(facturaDto.getTotalFactura());

        // 3. Asignar el Usuario
        facturaEnt.setUsuarioEnt(usuarioEnt); //FK

        // 4. Guardar Factura
        facturaRepository.save(facturaEnt);

        // 5. Retornar DTO, Entity → DTO
        facturaDto.setIdfactura(facturaEnt.getId_Factura());
        return facturaDto;
    }

    @Override
    public List<FacturaDTO> listarFacturasDto() {
        List<FacturaEntity> facturasEnt = facturaRepository.findAll();
        List<FacturaDTO> facturasDto = new ArrayList<>();

        for(FacturaEntity facturaEntity : facturasEnt) {
            //Entity → DTO
            FacturaDTO facturaDto = entityToDto(facturaEntity);
            facturasDto.add(facturaDto);
        }

        return facturasDto;
    }

    @Override
    public FacturaDTO actuzalizarFactura(Integer idFactura, FacturaDTO facturaDto) {
        FacturaEntity facturaEnt = facturaRepository.findById(idFactura).
                orElseThrow(() -> new RuntimeException("Factura con ID -> "+idFactura+" No encontrada"));

        // DTO → ENTITY (actualización)
        facturaEnt.setTotalFactura(facturaDto.getTotalFactura());

        // ENTITY → BASE DE DATOS
        FacturaEntity facturaActualizada = facturaRepository.save(facturaEnt);

        // ENTITY → DTO
        return entityToDto(facturaActualizada);
    }

    @Override
    public void eliminarFactura(Integer idFactura) {
        FacturaEntity facturaEnt = facturaRepository.findById(idFactura).
                orElseThrow(() -> new RuntimeException("Factura con ID -> "+idFactura+" No encontrada"));

        facturaRepository.delete(facturaEnt);

    }
}
