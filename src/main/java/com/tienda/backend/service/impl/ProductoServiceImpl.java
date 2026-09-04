package com.tienda.backend.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.tienda.backend.dto.ProductosDTO;
import com.tienda.backend.entity.ProductosEntity;
import com.tienda.backend.exception.RecursoDuplicadoException;
import com.tienda.backend.exception.RecursoNoEncontradoException;
import com.tienda.backend.mapper.IProductoMapper;
import com.tienda.backend.repository.IProductosRepository;
import com.tienda.backend.service.IProductoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductoServiceImpl implements IProductoService {

    @Autowired
    private IProductosRepository productoRepository;

    @Autowired
    private IProductoMapper productoMapper;

    @Async                                                                                                              // Este método corre en segundo plano, para que la página no se congele mientras se procesa el archivo
    public void guardarCsv(MultipartFile file) {

        try (Reader reader = new InputStreamReader(file.getInputStream());                                              // Obtiene los bytes del archivo y los convierte en caracteres
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {                             // Lee el archivo como CSV e ignora la primera fila (encabezados)

            List<String[]> filas = csvReader.readAll();                                                                 // Lee todas las filas del CSV: cada fila es un arreglo con los datos de una línea del Excel

            // Convierte cada fila en un Producto y elimina duplicados por nombre
            Map<String, ProductosEntity> productosSinDuplicados = filas.stream()
                    .map(fila -> {                                                                               // fila = una línea del archivo, por ejemplo ["Laptop", "1200", "Electrónica"]
                        ProductosEntity producto = new ProductosEntity();
                        producto.setNombreProducto(fila[0]);
                        producto.setPrecio(Double.parseDouble(fila[1]));
                        producto.setCategoria(fila[2]);
                        return producto;
                    })
                    .collect(Collectors.toMap(ProductosEntity::getNombreProducto,                                       // Clave: usa el nombre como clave única
                            producto -> producto,                                                          // Valor: el valor es el producto completo
                            (existente, duplicado) -> existente                              //  Si hay duplicados, conserva el primero
                    ));

            // Guarda todos los productos en la base de datos
            productoRepository.saveAll(productosSinDuplicados.values());

            // Registra la cantidad de productos guardados
            log.info("\n\n\tRespuesta: CSV procesado correctamente: {} productos guardados", productosSinDuplicados.size());

        } catch (Exception e) {
            // Registra cualquier error durante el procesamiento
            log.error("\n\n\tRespuesta: Error al procesar el archivo CSV: ", e);
        }
    }

    @Override
    public ProductosDTO agregarProducto(ProductosDTO productosDto) {
        // Buscar el producto por nombre de producto en la base de datos
        Optional<ProductosEntity> productoExistente = productoRepository.findByNombreProducto(productosDto.getNombreProducto());

        if (productoExistente.isPresent()) {
            throw new RecursoDuplicadoException("Error: El producto ya existe");
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
    @Transactional
    public ProductosDTO actualizarProducto(Long id_Productos, ProductosDTO productosDto) {

        ProductosEntity productosEnt = productoRepository.findById(id_Productos)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID --> "+id_Productos+" No encontrado"));

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
    @Transactional
    public void eliminarProducto(Long id_Productos) {

        // Se valida que el usuario exista antes de eliminar
        ProductosEntity productosEnt = productoRepository.findById(id_Productos)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID --> "+id_Productos+" No encontrado"));

        // Se elimina el producto
        productoRepository.delete(productosEnt);
    }
}

/*

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

*/
