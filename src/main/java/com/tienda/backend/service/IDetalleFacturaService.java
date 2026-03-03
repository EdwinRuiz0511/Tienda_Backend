package com.tienda.backend.service;

import com.tienda.backend.dto.DetalleFacturaDTO;

import java.util.List;

public interface IDetalleFacturaService {

    DetalleFacturaDTO agregarDetalleFactura(DetalleFacturaDTO detalleFacturaDto);

    List<DetalleFacturaDTO> agregarListaDeTallesFactura(List<DetalleFacturaDTO> listaDetallesFactura);
}
