package com.udc.matricula.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    // Para el prototipo basta con identificar al estudiante por su código,
    // sin necesidad todavía de una entidad Estudiante completa (login, datos personales, etc.).
    @Column(nullable = false, length = 20)
    private String codigoEstudiante;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime fechaInscripcion = LocalDateTime.now();
}