package com.udc.matricula.repository;

import com.udc.matricula.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

// Al extender JpaRepository, Spring Data JPA genera automáticamente
// (sin que escribas ni una línea de SQL) los métodos: save, findById,
// findAll, deleteById, etc. <Materia, Long> = entidad y tipo de su @Id.
public interface MateriaRepository extends JpaRepository<Materia, Long> {
}
