package com.pabei.Despensa.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponderSolicitudAmistadRequest {
    @NotNull(message = "El ID del destinatario es obligatorio.")
    @Positive(message = "El ID del destinatario debe ser un número positivo.")
    private Long destinatarioId;
}
