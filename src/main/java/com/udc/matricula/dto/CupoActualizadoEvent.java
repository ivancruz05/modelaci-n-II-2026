package com.udc.matricula.dto;

// Esto es lo que recibirá Angular cada vez que cambien los cupos de un grupo
public record CupoActualizadoEvent(Long grupoId, Integer cuposDisponibles, Integer cuposTotales) {
}