package com.tienda.backend.service.impl;

import com.tienda.backend.dto.DetalleFacturaDTO;
import com.tienda.backend.dto.FacturaDTO;
import com.tienda.backend.dto.UsuarioDTO;
import com.tienda.backend.entity.DetalleFacturaEntity;
import com.tienda.backend.entity.FacturaEntity;
import com.tienda.backend.entity.RolEntity;
import com.tienda.backend.entity.UsuarioEntity;
import com.tienda.backend.repository.IRolRepository;
import com.tienda.backend.repository.IUsuarioRepository;
import com.tienda.backend.security.dtoSecurity.RegisterRequestDTO;
import com.tienda.backend.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    IUsuarioRepository usuarioRepository;

    @Autowired
    IRolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



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
        usuarioDto.setId_Usuario(usuarioEnt.getId_Usuario()); //Ahora si se incluye la Identificacion por ya existe (la creó la BD)
        usuarioDto.setNombre(usuarioEnt.getNombre());
        usuarioDto.setApellido(usuarioEnt.getApellido());
        usuarioDto.setSexo(usuarioEnt.getSexo());
        usuarioDto.setTelefono(usuarioEnt.getTelefono());
        usuarioDto.setUsername(usuarioEnt.getUsername());

        return usuarioDto;
    }

    // Convierte un UsuarioEntity en UsuarioDTO incluyendo solo sus Facturas
    private UsuarioDTO entityToDto2(UsuarioEntity usuarioEnt) {
        UsuarioDTO usuarioDto = new UsuarioDTO();
        usuarioDto.setId_Usuario(usuarioEnt.getId_Usuario()); //Ahora si se incluye la Identificacion por ya existe (la creó la BD)
        usuarioDto.setNombre(usuarioEnt.getNombre());
        usuarioDto.setApellido(usuarioEnt.getApellido());
        usuarioDto.setSexo(usuarioEnt.getSexo());
        usuarioDto.setTelefono(usuarioEnt.getTelefono());

        // Inicializamos la lista donde se almacenarán las facturas
        usuarioDto.setListFacturaDTO(new ArrayList<>());

        // Recorre las facturas del usuario
        for(FacturaEntity facturaEnt : usuarioEnt.getListaFacturaEnt()){
            FacturaDTO facturaDto = new FacturaDTO();
            facturaDto.setId_Factura(facturaEnt.getId_Factura());
            facturaDto.setTotalFactura(facturaEnt.getTotalFactura());

            // Agrega la factura al usuario
            usuarioDto.getListFacturaDTO().add(facturaDto);
        }
        return usuarioDto;
    }

    // Convierte un UsuarioEntity en UsuarioDTO con Facturas y Detalles
    private UsuarioDTO entityToDto3(UsuarioEntity usuarioEnt) {
        UsuarioDTO usuarioDto = new UsuarioDTO();

        // NIVEL 1: Datos del Usuario (Entity → DTO)
        usuarioDto.setId_Usuario(usuarioEnt.getId_Usuario());
        usuarioDto.setNombre(usuarioEnt.getNombre());
        usuarioDto.setApellido(usuarioEnt.getApellido());
        usuarioDto.setSexo(usuarioEnt.getSexo());
        usuarioDto.setTelefono(usuarioEnt.getTelefono());

        // Inicializamos la lista donde se almacenarán las facturas
        usuarioDto.setListFacturaDTO(new ArrayList<>());

        // NIVEL 2: Recorrer Facturas del Usuario
        for (FacturaEntity facturaEnt : usuarioEnt.getListaFacturaEnt()) {
            FacturaDTO facturaDto = new FacturaDTO();
            facturaDto.setId_Factura(facturaEnt.getId_Factura());
            facturaDto.setTotalFactura(facturaEnt.getTotalFactura());

            // Inicializamos la lista de detalles de la factura
            facturaDto.setListaDetalleFacturaDTO(new ArrayList<>());

            // NIVEL 3: Recorrer Detalles de las Facturas del Usuario
            for (DetalleFacturaEntity detalleFacturaEnt : facturaEnt.getListaDetalleFacturaEnt()) {
                DetalleFacturaDTO detalleFacturaDto = new DetalleFacturaDTO();
                detalleFacturaDto.setId_DetalleFac(detalleFacturaEnt.getId_DetalleFac());                               // Toma los datos de la factura de la base de datos y pásalos al objeto que se enviará al cliente.
                detalleFacturaDto.setCantProductos(detalleFacturaEnt.getCantProductos());
                detalleFacturaDto.setTotal(detalleFacturaEnt.getTotal());

                detalleFacturaDto.setId_Factura(facturaEnt.getId_Factura());
                detalleFacturaDto.setId_Producto(detalleFacturaEnt.getProductosEnt().getId_Productos());
                detalleFacturaDto.setNombreProducto(detalleFacturaEnt.getProductosEnt().getNombreProducto());
                detalleFacturaDto.setCategoria(detalleFacturaEnt.getProductosEnt().getCategoria());
                detalleFacturaDto.setPrecio(detalleFacturaEnt.getProductosEnt().getPrecio());
                // detalleFacturaDto.setId_Usuario(facturaEnt.getUsuarioEnt().getId_Usuario());

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
    public void registrarUsuario(RegisterRequestDTO registerRequestDTO) {
        // 1. Validar si el username ya existe
        if (usuarioRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("❌ El username ya existe");
        }

        // 2. Buscar rol USER (por seguridad, todo usuario registrado es USER)
        RolEntity rolEnt = rolRepository.findByNombreRol("USER")
                .orElseThrow(() -> new RuntimeException("❌ Rol USER no existe"));

        // 3. Crear entidad Usuario:
        UsuarioEntity usuarioEnt = new UsuarioEntity();
        usuarioEnt.setNombre(registerRequestDTO.getNombre());
        usuarioEnt.setApellido(registerRequestDTO.getApellido());
        usuarioEnt.setUsername(registerRequestDTO.getUsername());
        usuarioEnt.setSexo(registerRequestDTO.getSexo());
        usuarioEnt.setTelefono(registerRequestDTO.getTelefono());

        // 4. Encriptar password antes de guardar
        usuarioEnt.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));

        //5. Asignar rol
        usuarioEnt.setRolEnt(rolEnt);

        // 6. Guardar en base de datos_
        usuarioRepository.save(usuarioEnt);
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
    public UsuarioDTO listar_Usuarios_Factu_PorId(Long id_Usuario) {
        Optional<UsuarioEntity> usuarioEntityOpc = usuarioRepository.findByIdConFacturas(id_Usuario);
        UsuarioDTO usuarioDto = null;

        if(usuarioEntityOpc.isPresent()){
            UsuarioEntity usuarioEnt = usuarioEntityOpc.get();
            usuarioDto = entityToDto2(usuarioEnt); //Entity → DTO
        }
        return usuarioDto;
    }

    //Metodo para listar un Usuario con sus Facturas y Detalles por su ID
    @Override
    public UsuarioDTO listar_Usuarios_Factu_Detall_PorId(Long id_Usuario) {
        UsuarioEntity usuarioEnt = usuarioRepository.findByIdConFacturasYDetalles(id_Usuario)
                .orElseThrow(() -> new RuntimeException("Usuario con ID -> "+id_Usuario+ " No encontrado"));

        return entityToDto3(usuarioEnt);
    }

    @Override
    public UsuarioDTO actuzalizarUsuario(Long id_Usuario, UsuarioDTO usuarioDto) {
        UsuarioEntity usuarioEnt = usuarioRepository.findById(id_Usuario)
                .orElseThrow(() -> new RuntimeException("Usuario con ID -> "+id_Usuario+ " No encontrado"));

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
    public void eliminarUsuario(Long id_Usuario) {
        // BASE DE DATOS → ENTITY
        // Se valida que el usuario exista antes de eliminar
        UsuarioEntity usuarioEnt = usuarioRepository.findById(id_Usuario).
                orElseThrow(() -> new RuntimeException("Usuario con ID -> "+ id_Usuario +" No encontrado"));

        // Se elimina el registro
        usuarioRepository.delete(usuarioEnt);
    }
}

