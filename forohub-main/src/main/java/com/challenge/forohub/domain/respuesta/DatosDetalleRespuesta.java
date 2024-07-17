package com.challenge.forohub.domain.respuesta;

import com.challenge.forohub.domain.shared.Status;

import java.time.LocalDateTime;

public record DatosDetalleRespuesta(
        Long id,
        String mensaje,
        String solucion,
        String autor,
        String topico,
        LocalDateTime fechaCreacion,
        Status status
) {
}
