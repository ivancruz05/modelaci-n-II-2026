package com.udc.matricula.repository;

import com.udc.matricula.model.Grupo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByMateriaId(Long materiaId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Grupo g where g.id = :id")
    Optional<Grupo> findByIdForUpdate(@Param("id") Long id);
}