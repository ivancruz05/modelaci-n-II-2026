package com.udc.matricula.controller;

import com.udc.matricula.dto.ActualizarGrupoRequest;
import com.udc.matricula.dto.CupoActualizadoEvent;
import com.udc.matricula.repository.InscripcionRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
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
    private final InscripcionRepository inscripcionRepository;
    private final SimpMessagingTemplate messagingTemplate;

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
    @PutMapping("/{id}")
    @Transactional
    public Grupo actualizar(@PathVariable Long id, @RequestBody ActualizarGrupoRequest request) {
        Grupo grupo = grupoRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("El grupo " + id + " no existe"));

        if (request.horario() != null) {
            grupo.setHorario(request.horario());
        }
        if (request.cuposTotales() != null) {
            int inscritos = inscripcionRepository.findByGrupoId(id).size();
            if (request.cuposTotales() < inscritos) {
                throw new IllegalStateException("El grupo ya tiene " + inscritos
                        + " inscritos; no puede tener menos cupos que eso");
            }
            grupo.setCuposTotales(request.cuposTotales());
            grupo.setCuposDisponibles(request.cuposTotales() - inscritos);
        }
        Grupo guardado = grupoRepository.save(grupo);

        messagingTemplate.convertAndSend("/topic/grupos/" + id,
                new CupoActualizadoEvent(id, guardado.getCuposDisponibles(), guardado.getCuposTotales()));
        return guardado;
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        if (!grupoRepository.existsById(id)) {
            throw new IllegalArgumentException("El grupo " + id + " no existe");
        }
        grupoRepository.deleteById(id);
    }
}