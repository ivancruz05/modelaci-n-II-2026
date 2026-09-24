package com.udc.matricula.controller;

import com.udc.matricula.dto.CrearGrupoRequest;
import com.udc.matricula.model.Grupo;
import com.udc.matricula.model.Materia;
import com.udc.matricula.repository.GrupoRepository;
import com.udc.matricula.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoRepository grupoRepository;
    private final MateriaRepository materiaRepository;

    @PostMapping
    public Grupo crear(@RequestBody CrearGrupoRequest request) {
        Materia materia = materiaRepository.findById(request.materiaId())
                .orElseThrow(() -> new IllegalArgumentException("La materia " + request.materiaId() + " no existe"));

        Grupo grupo = Grupo.builder()
                .materia(materia)
                .horario(request.horario())
                .cuposTotales(request.cuposTotales())
                .cuposDisponibles(request.cuposTotales()) // al crearlo, todos los cupos están libres
                .build();
        return grupoRepository.save(grupo);
    }

    @GetMapping
    public List<Grupo> listar() {
        return grupoRepository.findAll();
    }
}