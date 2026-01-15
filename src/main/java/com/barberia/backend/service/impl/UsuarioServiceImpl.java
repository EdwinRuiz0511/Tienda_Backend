package com.barberia.backend.service.impl;

import com.barberia.backend.dto.DetalleFacturaDTO;
import com.barberia.backend.dto.FacturaDTO;
import com.barberia.backend.dto.UsuarioDTO;
import com.barberia.backend.entity.DetalleFacturaEntity;
import com.barberia.backend.entity.FacturaEntity;
import com.barberia.backend.entity.UsuarioEntity;
import com.barberia.backend.repository.IUsuarioRepository;
import com.barberia.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    IUsuarioRepository usuarioRepository;

    // Frontend → DTO → Entity → Base de datos
    private UsuarioEntity dtoToEntity (UsuarioDTO usuarioDto){
        UsuarioEntity usuarioEnt = new UsuarioEntity();              // No copia el ID porque aún no existe, en este punto el usuario no esta en la base de datos, la Entity esta solo en memoria
        usuarioEnt.setNombre(usuarioDto.getNombre());
        usuarioEnt.setApellido(usuarioDto.getApellido());
        usuarioEnt.setSexo(usuarioDto.getSexo());
        usuarioEnt.setTelefono(usuarioDto.getTelefono());

        return usuarioEnt;
    }

    // Base de datos → Entity → DTO → Frontend
    private UsuarioDTO entityToDto(UsuarioEntity usuarioEnt) {
        UsuarioDTO usuarioDto = new UsuarioDTO();
        usuarioDto.setIdUsuario(usuarioEnt.getId_Usuario()); //Ahora si se incluye la Identificacion por ya existe (la creó la BD)
        usuarioDto.setNombre(usuarioEnt.getNombre());
        usuarioDto.setApellido(usuarioEnt.getApellido());
        usuarioDto.setSexo(usuarioEnt.getSexo());
        usuarioDto.setTelefono(usuarioEnt.getTelefono());

        return usuarioDto;
    }

    // Convierte un UsuarioEntity en UsuarioDTO incluyendo solo sus Facturas
    private UsuarioDTO entityToDto2(UsuarioEntity usuarioEnt) {
        UsuarioDTO usuarioDto = new UsuarioDTO();
        usuarioDto.setIdUsuario(usuarioEnt.getId_Usuario()); //Ahora si se incluye la Identificacion por ya existe (la creó la BD)
        usuarioDto.setNombre(usuarioEnt.getNombre());
        usuarioDto.setApellido(usuarioEnt.getApellido());
        usuarioDto.setSexo(usuarioEnt.getSexo());
        usuarioDto.setTelefono(usuarioEnt.getTelefono());
        usuarioDto.setListFacturaDTO(new ArrayList<>());

        // Recorre las facturas del usuario
        for(FacturaEntity facturaEnt : usuarioEnt.getFacturaEnt()){
            FacturaDTO facturaDto = new FacturaDTO();
            facturaDto.setIdfactura(facturaEnt.getId_Factura());
            facturaDto.setTotalFactura(facturaEnt.getTotalFactura());

            // Agrega la factura al usuario
            usuarioDto.getListFacturaDTO().add(facturaDto);
        }
        return usuarioDto;
    }

    // Convierte un UsuarioEntity en UsuarioDTO con Facturas y Detalles
    private UsuarioDTO entityToDto3(UsuarioEntity usuarioEnt) {
        UsuarioDTO usuarioDto = new UsuarioDTO();
        // Copiamos los datos básicos del usuario (Entity → DTO)
        usuarioDto.setIdUsuario(usuarioEnt.getId_Usuario());
        usuarioDto.setNombre(usuarioEnt.getNombre());
        usuarioDto.setApellido(usuarioEnt.getApellido());
        usuarioDto.setSexo(usuarioEnt.getSexo());
        usuarioDto.setTelefono(usuarioEnt.getTelefono());
        // Inicializamos la lista donde se almacenarán las facturas
        usuarioDto.setListFacturaDTO(new ArrayList<>());

        // Recorre las facturas del usuario
        for (FacturaEntity facturaEnt : usuarioEnt.getFacturaEnt()) {// Creamos el DTO de la factura
            FacturaDTO facturaDto = new FacturaDTO();
            facturaDto.setIdfactura(facturaEnt.getId_Factura());
            facturaDto.setTotalFactura(facturaEnt.getTotalFactura());

            // Inicializamos la lista de detalles de la factura// Creamos lista de DETALLES
            facturaDto.setListaDetalleFacturaDTO(new ArrayList<>());

            // Recorre los detalles de cada factura
            for (DetalleFacturaEntity detalleFacturaEnt : facturaEnt.getListaDetalleFacturaEnt()) {
                DetalleFacturaDTO detalleFacturaDto = new DetalleFacturaDTO();
                detalleFacturaDto.setId_DetalleFac(detalleFacturaEnt.getId_DetalleFac());
                detalleFacturaDto.setCantProductos(detalleFacturaEnt.getCantProductos());
                detalleFacturaDto.setTotal(detalleFacturaEnt.getTotal());

                // Agrega el detalle a la factura
                facturaDto.getListaDetalleFacturaDTO().add(detalleFacturaDto);
            }
            // Agregamos la factura (con sus detalles) al usuario
            usuarioDto.getListFacturaDTO().add(facturaDto);
        }
        // Retornamos el UsuarioDTO completo (usuario + facturas + detalles)
        return usuarioDto;
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public UsuarioDTO agregarUsuario(UsuarioDTO usuarioDto) {
        UsuarioEntity usuarioEnt = usuarioRepository.save(dtoToEntity(usuarioDto));
        usuarioDto.setIdUsuario(usuarioEnt.getId_Usuario());
        return usuarioDto;
    }

    @Override
    public List<UsuarioDTO> listarUsuarios() {

        List<UsuarioEntity> usuariosEnt = usuarioRepository.findAll();
        List<UsuarioDTO> usuariosDto = new ArrayList<>();

        // ENTITY → DTO (uno por uno)
        for(UsuarioEntity usuarioEnt : usuariosEnt) {
            UsuarioDTO usuarioDto = entityToDto(usuarioEnt);
            usuariosDto.add(usuarioDto);
        }
        // DTO → FRONTEND
        return usuariosDto;
    }

    // Metodo para listar las Facturas de un Usuario por su ID
    @Override
    public UsuarioDTO listar_Usuarios_Factu_PorId(Long idUsuario) {
        Optional<UsuarioEntity> usuarioEntityOpc = usuarioRepository.findById(idUsuario);
        UsuarioDTO usuarioDto = null;

        if(usuarioEntityOpc.isPresent()){
            UsuarioEntity usuarioEnt = usuarioEntityOpc.get();
            usuarioDto = entityToDto2(usuarioEnt); //Entity → DTO
        }
        return usuarioDto;
    }

    //Metodo para listar un Usuario con sus Facturas y Detalles por su ID
    @Override
    public UsuarioDTO listar_Usuarios_Factu_Detall_PorId(Long idUsuario) {
        Optional<UsuarioEntity> usuarioEntityOpc = usuarioRepository.findById(idUsuario);
        UsuarioDTO usuarioDto = null;

        if (usuarioEntityOpc.isPresent()) {
            UsuarioEntity usuarioEnt = usuarioEntityOpc.get();
            usuarioDto = entityToDto3(usuarioEnt);
        }
        return usuarioDto;
    }

    @Override
    public UsuarioDTO actuzalizarUsuario(Long idUsuario, UsuarioDTO usuarioDto) {
        UsuarioEntity usuarioEnt = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario con ID -> "+idUsuario+ " No encontrado"));

        // DTO → ENTITY (actualización)
        usuarioEnt.setNombre(usuarioDto.getNombre());
        usuarioEnt.setApellido(usuarioDto.getApellido());
        usuarioEnt.setSexo(usuarioDto.getSexo());
        usuarioEnt.setTelefono(usuarioDto.getTelefono());

        // ENTITY → BASE DE DATOS
        UsuarioEntity usuarioActualizado = usuarioRepository.save(usuarioEnt);

        // ENTITY → DTO
        return entityToDto(usuarioActualizado);
    }

    @Override
    public void eliminarUsuario(Long idUsuario) {
        // BASE DE DATOS → ENTITY
        // Se valida que el usuario exista antes de eliminar
        UsuarioEntity usuarioEnt = usuarioRepository.findById(idUsuario).
                orElseThrow(() -> new RuntimeException("Usuario con ID -> "+idUsuario+" No encontrado"));

        // Se elimina el registro
        usuarioRepository.delete(usuarioEnt);
    }
}

