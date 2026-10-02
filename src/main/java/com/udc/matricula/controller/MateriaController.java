package com.udc.matricula.controller;

import com.udc.matricula.dto.CrearMateriaRequest;
import com.udc.matricula.model.Materia;
import com.udc.matricula.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaRepository materiaRepository;

    @PostMapping
    public Materia crear(@RequestBody CrearMateriaRequest request) {
        Materia materia = Materia.builder()
                .nombre(request.nombre())
                .codigo(request.codigo())
                .build();
        return materiaRepository.save(materia);
    }

    @GetMapping
    public List<Materia> listar() {
        return materiaRepository.findAll();
    }

    @PutMapping("/{id}")
    public Materia actualizar(@PathVariable Long id, @RequestBody CrearMateriaRequest request) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La materia " + id + " no existe"));
        if (request.nombre() != null) materia.setNombre(request.nombre());
        if (request.codigo() != null) materia.setCodigo(request.codigo());
        return materiaRepository.save(materia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new IllegalArgumentException("La materia " + id + " no existe");
        }
        materiaRepository.deleteById(id);
    }
}