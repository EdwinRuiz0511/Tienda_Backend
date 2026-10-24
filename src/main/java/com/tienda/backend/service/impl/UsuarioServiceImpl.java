package com.tienda.backend.service.impl;

import com.tienda.backend.dto.UsuarioDTO;
import com.tienda.backend.entity.RolEntity;
import com.tienda.backend.entity.UsuarioEntity;
import com.tienda.backend.exception.RecursoDuplicadoException;
import com.tienda.backend.exception.RecursoNoEncontradoException;
import com.tienda.backend.mapper.IUsuarioMapper;
import com.tienda.backend.repository.IRolRepository;
import com.tienda.backend.repository.IUsuarioRepository;
import com.tienda.backend.security.dtoSecurity.RegisterRequestDTO;
import com.tienda.backend.security.serviceSecurity.SecurityUtils;
import com.tienda.backend.service.IUsuarioService;
import com.tienda.backend.utils.RolesConstantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private IUsuarioRepository iUsuarioRepository;

    @Autowired
    private IRolRepository iRolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IUsuarioMapper iUsuarioMapper;

    @Autowired
    private SecurityUtils securityUtils;

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void registrarUsuario(RegisterRequestDTO registerRequestDTO) {
        // 1. Validar si el username ya existe
        if (iUsuarioRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()) {
            throw new RecursoDuplicadoException("El Username --> °"+registerRequestDTO.getUsername()+"° YA existe");
        }

        // 2. Buscar rol USER (por seguridad, todo usuario registrado es USER)
        RolEntity rolEnt = iRolRepository.findByNombreRol(RolesConstantes.USER)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol --> " + RolesConstantes.USER + " no existe"));

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
        iUsuarioRepository.save(usuarioEnt);
    }

    @Override
    public List<UsuarioDTO> listarUsuarios() {

        //Listamos todos los Usuarios desde la BD
        List<UsuarioEntity> usuariosEnt = iUsuarioRepository.findAll();

        // DTO → FRONTEND
        return iUsuarioMapper.toDTOListBasico(usuariosEnt);
    }

    // Metodo para listar las Facturas de un Usuario por su ID
    @Override
    public UsuarioDTO listar_Usuarios_Factu_PorId(Long id_Usuario) {

        //Buscamos al Usuario por su Id en la BD
        UsuarioEntity usuarioEnt = iUsuarioRepository.findByIdConFacturas(id_Usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID -> "+id_Usuario+" No encontrado"));

        // Rechaza si quien pregunta no es ni el dueño de estos datos ni un ADMIN
        validarPropietarioOAdmin(usuarioEnt);

        // DTO → FRONTEND
        return iUsuarioMapper.toDTOConFacturas(usuarioEnt);
    }

    //Metodo para listar un Usuario con sus Facturas y Detalles por su ID
    @Override
    public UsuarioDTO listar_Usuarios_Factu_Detall_PorId(Long id_Usuario) {

        //Buscamos al Usuario por su Id en la BD
        UsuarioEntity usuarioEnt = iUsuarioRepository.findByIdConFacturasYDetalles(id_Usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID -> "+id_Usuario+ " No encontrado"));

        // Rechaza si quien pregunta no es ni el dueño de estos datos ni un ADMIN
        validarPropietarioOAdmin(usuarioEnt);

        // DTO → FRONTEND
        return iUsuarioMapper.toDTOCompleto(usuarioEnt);
    }

    @Override
    @Transactional
    public UsuarioDTO actuzalizarUsuario(Long id_Usuario, UsuarioDTO usuarioDto) {

        UsuarioEntity usuarioEnt = iUsuarioRepository.findById(id_Usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID -> "+id_Usuario+ " No encontrado"));

        // DTO → ENTITY (actualización)
        iUsuarioMapper.actualizarUsuario(usuarioDto, usuarioEnt);

        // ENTITY → BASE DE DATOS
        UsuarioEntity usuarioActualizado = iUsuarioRepository.save(usuarioEnt);

        // ENTITY → DTO
        return iUsuarioMapper.toDTOBasico(usuarioActualizado);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id_Usuario) {
        // BASE DE DATOS → ENTITY
        // Se valida que el usuario exista antes de eliminar
        UsuarioEntity usuarioEnt = iUsuarioRepository.findById(id_Usuario).
                orElseThrow(() -> new RecursoNoEncontradoException("Usuario con ID -> "+ id_Usuario +" No encontrado"));

        // Se elimina el registro
        iUsuarioRepository.delete(usuarioEnt);
    }

    // Lanza AccessDeniedException si quien pregunta no es ni el dueño de los datos ni un ADMIN
    private void validarPropietarioOAdmin(UsuarioEntity usuarioEnt) {
        String usernameAutenticado = securityUtils.obtenerUsernameAutenticado();
        boolean esPropietario = usuarioEnt.getUsername().equals(usernameAutenticado);

        if (!securityUtils.esAdministrador() && !esPropietario) {
            throw new AccessDeniedException("No tiene permiso para consultar la informacion de otro Usuario");
        }
    }
}

