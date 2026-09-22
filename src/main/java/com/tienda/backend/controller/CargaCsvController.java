package com.tienda.backend.controller;

import com.tienda.backend.dto.CargaCsvDTO;
import com.tienda.backend.service.ICargaCsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carga-csv")
public class CargaCsvController {

    @Autowired
    private ICargaCsvService iCargaCsvService;

    @GetMapping("estado/{id_Carga}")
    public ResponseEntity<CargaCsvDTO> consultarEstado(@PathVariable Long id_Carga) {
        CargaCsvDTO cargaCsvDto = iCargaCsvService.consultarEstado(id_Carga);
        return ResponseEntity.ok(cargaCsvDto);
    }
}
