package com.udc.matricula.service;

import com.udc.matricula.model.Grupo;
import com.udc.matricula.model.Inscripcion;
import com.udc.matricula.repository.GrupoRepository;
import com.udc.matricula.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Lombok genera el constructor con estos 2 campos "final" -> inyección de dependencias
public class InscripcionService {

    private final GrupoRepository grupoRepository;
    private final InscripcionRepository inscripcionRepository;

    // @Transactional: todo lo de adentro (leer el grupo, restar el cupo, guardar,
    // crear la inscripción) ocurre como una sola operación indivisible.
    // Si algo falla a mitad de camino, se deshace todo — nunca queda a medias.
    @Transactional
    public Inscripcion inscribir(Long grupoId, String codigoEstudiante) {
        Grupo grupo = grupoRepository.findByIdForUpdate(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("El grupo " + grupoId + " no existe"));

        if (grupo.getCuposDisponibles() <= 0) {
            throw new IllegalStateException("No quedan cupos disponibles en este grupo");
        }

        grupo.setCuposDisponibles(grupo.getCuposDisponibles() - 1);
        grupoRepository.save(grupo);

        Inscripcion inscripcion = Inscripcion.builder()
                .grupo(grupo)
                .codigoEstudiante(codigoEstudiante)
                .build();
        inscripcionRepository.save(inscripcion);

        // TODO (próximo paso): avisar por WebSocket que grupo.getCuposDisponibles() cambió

        return inscripcion;
    }

    @Transactional
    public void cancelar(Long inscripcionId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("La inscripción " + inscripcionId + " no existe"));

        Grupo grupo = grupoRepository.findByIdForUpdate(inscripcion.getGrupo().getId())
                .orElseThrow();

        grupo.setCuposDisponibles(grupo.getCuposDisponibles() + 1);
        grupoRepository.save(grupo);

        inscripcionRepository.delete(inscripcion);

        // TODO (próximo paso): avisar por WebSocket que grupo.getCuposDisponibles() cambió
    }
}