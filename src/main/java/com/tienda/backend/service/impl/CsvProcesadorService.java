package com.tienda.backend.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.tienda.backend.entity.ProductosEntity;
import com.tienda.backend.repository.IProductosRepository;
import com.tienda.backend.service.ICargaCsvService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CsvProcesadorService — clase separada SOLO por una razón técnica: el problema de auto-invocación con @Async.
 *
 * ¿QUÉ PROBLEMA RESUELVE ESTA CLASE?
 * Spring solo puede interceptar un método @Async cuando la llamada viene "desde afuera" de la clase
 * (a través del proxy que Spring genera para esa clase). Si guardarCsv() viviera dentro de
 * ProductoServiceImpl, y otro método de ESA MISMA clase (como iniciarCargaCsv) lo llamara con
 * "this.guardarCsv(...)" (aunque no se escriba explícito), esa llamada NUNCA pasa por el proxy —
 * es una llamada interna, directa, de Java puro. Resultado: @Async se ignora SILENCIOSAMENTE,
 * sin ningún error visible, y el método se ejecuta de forma síncrona, bloqueando la petición HTTP.
 *
 * La solución: mover el método @Async a esta clase completamente distinta. Ahora, cuando
 * ProductoServiceImpl llama a csvProcesadorService.guardarCsv(...), esa llamada SALE de
 * ProductoServiceImpl y ENTRA "desde afuera" al proxy de CsvProcesadorService — ahí Spring
 * SÍ puede interceptarla correctamente y mandarla a un hilo aparte (@Async funciona de verdad).
 *
 * ¿POR QUÉ ESTA CLASE NO TIENE INTERFAZ (a diferencia de los demás services)?
 * Una interfaz tiene sentido cuando representa un concepto de negocio que otros (controllers,
 * otros services) necesitan consumir de forma desacoplada, o cuando podría existir más de una
 * implementación distinta en el futuro. Esta clase no es eso: existe únicamente por la razón
 * técnica explicada arriba (resolver el problema del proxy), nadie más la consume aparte de
 * ProductoServiceImpl, y nunca habrá una "segunda implementación" de este procesamiento puntual.
 * Crear una interfaz aquí solo agregaría ceremonia extra, sin ningún beneficio real.
 */

@Service
@Slf4j
public class CsvProcesadorService {

    @Autowired
    private IProductosRepository iProductosRepository;

    @Autowired
    private ICargaCsvService iCargaCsvService;

    @Async                                                                                                              // Este método corre en segundo plano, para que la página no se congele mientras se procesa el archivo
    public void guardarCsv(MultipartFile file, Long idCarga) {

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
            iProductosRepository.saveAll(productosSinDuplicados.values());                                              // Utilizamos .values ya que extrae solo los productos (valores) del mapa, porque saveAll() espera una coleccion, no un Map

            // Actualiza el registro de seguimiento: pasa de PENDIENTE a EXITOSOfi
            // guardando cuántas filas traía el archivo original y cuántos productos se guardaron de verdad
            iCargaCsvService.marcarComoExitosa(idCarga, filas.size(), productosSinDuplicados.size());


            // Registra la cantidad de productos guardados
            log.info("Respuesta: CSV procesado correctamente: {} productos originales", filas.size());
            log.info("Respuesta: CSV procesado correctamente: {} productos guardados", productosSinDuplicados.size());

        } catch (DataIntegrityViolationException e) {
            log.error(("Porducto duplicado al procesar el CSV: " + e));
            iCargaCsvService.marcarComoError(idCarga, "Ya existe un producto con ese nombre en la base de datos");

        } catch (NumberFormatException e) {
            log.error("Precio inválido al procesar el CSV: " + e);
            iCargaCsvService.marcarComoError(idCarga, "Uno de los precios en el archivo no es un número válido");

        } catch (IOException e) {
            log.error("Error al leer el archivo CSV: " + e);
            iCargaCsvService.marcarComoError(idCarga, "No se pudo leer el archivo, intenta subirlo de nuevo");

        } catch (Exception e) {
            // Registra cualquier error durante el procesamiento
            log.error("Respuesta: Error al procesar el archivo CSV: ", e);

            // Actualiza el registro de seguimiento: pasa de PENDIENTE a ERROR,
            // para que cualquier admin pueda consultar qué pasó, sin necesidad de ver la consola del servidor
            iCargaCsvService.marcarComoError(idCarga, "Error al procesar el archivo");
        }
    }
}
