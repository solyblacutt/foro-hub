package com.challenge.forohub.domain.topico;

import java.time.LocalDateTime;

public record DatosRespuestaTopicoActualizacion(
        Long id,
        String titulo,
        String mensaje,
        String autor,
        String status,
        LocalDateTime fechaActualizacion
) {
}
