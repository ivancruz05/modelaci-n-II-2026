package com.udc.matricula.service;

import com.udc.matricula.dto.CupoActualizadoEvent;
import com.udc.matricula.model.Grupo;
import com.udc.matricula.model.Inscripcion;
import com.udc.matricula.repository.GrupoRepository;
import com.udc.matricula.repository.InscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final GrupoRepository grupoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final SimpMessagingTemplate messagingTemplate;

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

        avisarCambioDeCupos(grupo);

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

        avisarCambioDeCupos(grupo);
    }

    // Publica el nuevo estado del grupo en su propio canal: /topic/grupos/{id}

    // lo recibe al instante, sin pedirlo.
    private void avisarCambioDeCupos(Grupo grupo) {
        messagingTemplate.convertAndSend(
                "/topic/grupos/" + grupo.getId(),
                new CupoActualizadoEvent(grupo.getId(), grupo.getCuposDisponibles(), grupo.getCuposTotales())
        );
    }
}