package com.barberia.backend.service;

import com.barberia.backend.dto.FacturaDTO;
import com.barberia.backend.dto.UsuarioDTO;

import java.util.List;

public interface IFacturaService {

    FacturaDTO agregarFactura(FacturaDTO facturaDto);

    List<FacturaDTO> listarFacturasDto();

    FacturaDTO actuzalizarFactura(Integer idFactura, FacturaDTO facturaDto);

    void eliminarFactura(Integer idFactura);
}
