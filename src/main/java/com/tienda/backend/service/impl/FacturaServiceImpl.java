package com.tienda.backend.service.impl;

import com.tienda.backend.dto.FacturaDTO;
import com.tienda.backend.entity.FacturaEntity;
import com.tienda.backend.entity.UsuarioEntity;
import com.tienda.backend.repository.IFacturaRepository;
import com.tienda.backend.repository.IUsuarioRepository;
import com.tienda.backend.service.IFacturaService;
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
        facturaDto.setId_Factura(facturaEnt.getId_Factura());
        facturaDto.setTotalFactura(facturaEnt.getTotalFactura()); //Ahora si se incluye la Identificacion por ya existe (la creó la BD)

        return facturaDto;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public FacturaDTO agregarFactura(FacturaDTO facturaDto) {
        // 1. Buscar el usuario REAL desde la BD
        UsuarioEntity usuarioEnt = usuarioRepository.findById(facturaDto.getId_Usuario()).
                orElseThrow(() -> new RuntimeException("Usuario no existe"));

        // 2. Crear la Factura, DTO → Entity
        FacturaEntity facturaEnt = new FacturaEntity();
        facturaEnt.setTotalFactura(facturaDto.getTotalFactura()); //--> Esto lo calculamos y lo guardamos en BD al momento de crear el DetalleFacturaServiceImpl

        // 3. Asignar el Usuario
        facturaEnt.setUsuarioEnt(usuarioEnt); //FK

        // 4. Guardar Factura
        facturaRepository.save(facturaEnt);

        // 5. Retornar DTO, Entity → DTO
        FacturaDTO respuesta = new FacturaDTO();
        respuesta.setId_Factura(facturaEnt.getId_Factura());
        respuesta.setId_Usuario(facturaEnt.getUsuarioEnt().getId_Usuario());
        respuesta.setTotalFactura(facturaEnt.getTotalFactura());

        return respuesta;
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
    public FacturaDTO actuzalizarFactura(Integer id_Factura, FacturaDTO facturaDto) {
        FacturaEntity facturaEnt = facturaRepository.findById(id_Factura).
                orElseThrow(() -> new RuntimeException("Factura con ID -> "+ id_Factura +" No encontrada"));

        // DTO → ENTITY (actualización)
        facturaEnt.setTotalFactura(facturaDto.getTotalFactura());

        // ENTITY → BASE DE DATOS
        FacturaEntity facturaActualizada = facturaRepository.save(facturaEnt);

        // ENTITY → DTO
        return entityToDto(facturaActualizada);
    }

    @Override
    public void eliminarFactura(Integer id_Factura) {
        FacturaEntity facturaEnt = facturaRepository.findById(id_Factura).
                orElseThrow(() -> new RuntimeException("Factura con ID -> "+ id_Factura +" No encontrada"));

        facturaRepository.delete(facturaEnt);

    }
}
