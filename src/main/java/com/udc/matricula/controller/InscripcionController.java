package com.udc.matricula.controller;

import com.udc.matricula.dto.InscribirRequest;
import com.udc.matricula.model.Inscripcion;
import com.udc.matricula.service.InscripcionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @PostMapping
    public Inscripcion inscribir(@RequestBody InscribirRequest request) {
        return inscripcionService.inscribir(request.grupoId(), request.codigoEstudiante());
    }

    @DeleteMapping("/{id}")
    public void cancelar(@PathVariable Long id) {
        inscripcionService.cancelar(id);
    }
}