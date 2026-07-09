package com.tienda.backend.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.tienda.backend.dto.ProductosDTO;
import com.tienda.backend.entity.ProductosEntity;
import com.tienda.backend.mapper.IProductoMapper;
import com.tienda.backend.repository.IProductosRepository;
import com.tienda.backend.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ProductoServiceImpl implements IProductoService {

    @Autowired
    private IProductosRepository productoRepository;

    @Autowired
    private IProductoMapper productoMapper;

    @Async
    public void guardarCsv(MultipartFile file) {

        ExecutorService executor = Executors.newFixedThreadPool(2); // 👈 2 hilos

        try (
                Reader reader = new InputStreamReader(file.getInputStream());
                CSVReader csvReader = new CSVReaderBuilder(reader)
                        .withSkipLines(1) // saltar encabezado
                        .build();
        ) {

            List<String[]> filas = csvReader.readAll();

            for (String[] fila : filas) {

                ProductosEntity productoEnt = new ProductosEntity();
                productoEnt.setNombreProducto(fila[0]);
                productoEnt.setPrecio(Double.parseDouble(fila[1]));
                productoEnt.setCategoria(fila[2]);

                // 👇 Cada producto se envía a un hilo del pool
                executor.execute(() -> {
                    String hilo = Thread.currentThread().getName();
                    System.out.println("🧵 Hilo: " + hilo
                            + " → Guardando producto: " + productoEnt.getNombreProducto());

                    productoRepository.save(productoEnt);
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // cerrar pool
        }
    }

    @Override
    public ProductosDTO agregarProducto(ProductosDTO productosDto) {
        // Buscar el producto por nombre de producto en la base de datos
        Optional<ProductosEntity> productoExistente = productoRepository.findByNombreProducto(productosDto.getNombreProducto());

        if (productoExistente.isPresent()) {
            throw new RuntimeException("Error: El producto ya existe");
        }

        // Creamos el producto dto -> entity
        ProductosEntity productosEnt = productoMapper.toEntity(productosDto);

        //Guardamos en base de datos
        ProductosEntity guardado = productoRepository.save(productosEnt);

        // entity -> dto
        ProductosDTO respuesta = productoMapper.toDTO(guardado);

        return respuesta;
    }

    @Override
    public List<ProductosDTO> listarProductos() {

        List<ProductosEntity> productosEnt = productoRepository.findAll();

        // DTO → FRONTEND
        return productoMapper.toDTOList(productosEnt);
    }

    @Override
    public ProductosDTO actualizarProducto(Long id_Productos, ProductosDTO productosDto) {
        ProductosEntity productosEnt = productoRepository.findById(id_Productos)
                .orElseThrow(() -> new RuntimeException("Producto con ID --> "+id_Productos+" No encontrado"));

        // DTO → ENTITY (actualización)
        productoMapper.actualizarProducto(productosDto, productosEnt);

        // ENTITY → BASE DE DATOS
        ProductosEntity productoActualizado = productoRepository.save(productosEnt);

        // ENTITY → DTO
        ProductosDTO respuesta = productoMapper.toDTO(productoActualizado);

        // DTO -> FRONTEND
        return respuesta;
    }

    @Override
    public void eliminarProducto(Long id_Productos) {

        // Se valida que el usuario exista antes de eliminar
        ProductosEntity productosEnt = productoRepository.findById(id_Productos)
                .orElseThrow(() -> new RuntimeException("Producto con ID --> "+id_Productos+" No encontrado"));

        // Se elimina el producto
        productoRepository.delete(productosEnt);
    }
}
