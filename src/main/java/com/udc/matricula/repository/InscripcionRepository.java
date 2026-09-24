package com.udc.matricula.repository;

import com.udc.matricula.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // SELECT * FROM inscripciones WHERE grupo_id = ?
    List<Inscripcion> findByGrupoId(Long grupoId);

    // SELECT * FROM inscripciones WHERE codigo_estudiante = ?
    List<Inscripcion> findByCodigoEstudiante(String codigoEstudiante);
}
