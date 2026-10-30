package com.tienda.backend.service.impl;

import com.tienda.backend.dto.ProductosDTO;
import com.tienda.backend.entity.ProductosEntity;
import com.tienda.backend.entity.UsuarioEntity;
import com.tienda.backend.exception.RecursoDuplicadoException;
import com.tienda.backend.exception.RecursoEnUsoException;
import com.tienda.backend.exception.RecursoNoEncontradoException;
import com.tienda.backend.mapper.IProductoMapper;
import com.tienda.backend.repository.IDetalleFacturaRepository;
import com.tienda.backend.repository.IProductosRepository;
import com.tienda.backend.repository.IUsuarioRepository;
import com.tienda.backend.service.ICargaCsvService;
import com.tienda.backend.service.IProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductoServiceImpl implements IProductoService {

    @Autowired
    private IProductosRepository iProductosRepository;

    @Autowired
    private IUsuarioRepository iUsuarioRepository;

    @Autowired
    private ICargaCsvService iCargaCsvService;

    @Autowired
    private CsvProcesadorService csvProcesadorService;

    @Autowired
    IDetalleFacturaRepository iDetalleFacturaRepository;

    @Autowired
    private IProductoMapper productoMapper;

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public Long iniciarCargaCsv(MultipartFile file) {

        // 1. Averiguar quien esta haciendo la peticion
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UsuarioEntity usuarioEnt = iUsuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario --> °" + username + "° No fue encontrado"));

        // 2. Crear el registro de seguimiento en estado PENDIENTE
        String nombreArchivo = file.getOriginalFilename();
        Long idCarga = iCargaCsvService.crearCargaPendiente(nombreArchivo, usuarioEnt);

        // 3. Disparar el procesamiento real, en segundo plano
        csvProcesadorService.guardarCsv(file, idCarga);

        //4. Devolver el ID inmediatamente, sin esperar a que guardarCsv termine
        return idCarga;
    }

    @Override
    public ProductosDTO agregarProducto(ProductosDTO productosDto) {
        // Buscar el producto por nombre de producto en la base de datos
        Optional<ProductosEntity> productoExistente = iProductosRepository.findByNombreProducto(productosDto.getNombreProducto());

        if (productoExistente.isPresent()) {
            throw new RecursoDuplicadoException("El producto --> °"+productosDto.getNombreProducto()+"° YA existe");
        }

        // Creamos el producto dto -> entity
        ProductosEntity productosEnt = productoMapper.toEntity(productosDto);

        //Guardamos en base de datos
        ProductosEntity guardado = iProductosRepository.save(productosEnt);

        // entity -> dto
        ProductosDTO respuesta = productoMapper.toDTO(guardado);

        return respuesta;
    }

    @Override
    public List<ProductosDTO> listarProductos() {

        List<ProductosEntity> productosEnt = iProductosRepository.findAll();

        // DTO → FRONTEND
        return productoMapper.toDTOList(productosEnt);
    }

    @Override
    @Transactional
    public ProductosDTO actualizarProducto(Long id_Productos, ProductosDTO productosDto) {

        ProductosEntity productosEnt = iProductosRepository.findById(id_Productos)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID --> "+id_Productos+" No encontrado"));

        // DTO → ENTITY (actualización)
        productoMapper.actualizarProducto(productosDto, productosEnt);

        // ENTITY → BASE DE DATOS
        ProductosEntity productoActualizado = iProductosRepository.save(productosEnt);

        // ENTITY → DTO
        ProductosDTO respuesta = productoMapper.toDTO(productoActualizado);

        // DTO -> FRONTEND
        return respuesta;
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id_Productos) {

        // Se valida que el producto exista antes de eliminar
        ProductosEntity productosEnt = iProductosRepository.findById(id_Productos)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID --> "+id_Productos+" No encontrado"));

        long cantidadDetalles = iDetalleFacturaRepository.contarDetallesPorProducto(id_Productos);
        if (cantidadDetalles > 0) {
            throw new RecursoEnUsoException("No se puede eliminar el producto °"+productosEnt.getNombreProducto()+"° con ID --> "+id_Productos+" porque tiene "+cantidadDetalles+" detalles de factura registrados");
        }

        // Se elimina el producto
        iProductosRepository.delete(productosEnt);
    }
}
