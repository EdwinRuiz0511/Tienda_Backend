package com.tienda.backend.service;

import com.tienda.backend.dto.FacturaDTO;


import java.util.List;

public interface IFacturaService {

    FacturaDTO agregarFactura(FacturaDTO facturaDto);

    List<FacturaDTO> listarFacturasDto();

    FacturaDTO actuzalizarFactura(Integer id_Factura, FacturaDTO facturaDto);

    void eliminarFactura(Integer id_Factura);
}
