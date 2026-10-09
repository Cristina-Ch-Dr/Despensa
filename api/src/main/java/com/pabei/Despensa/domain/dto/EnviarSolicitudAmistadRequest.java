package com.pabei.Despensa.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarSolicitudAmistadRequest {
    @NotNull(message = "El ID del solicitante es obligatorio.")
    @Positive(message = "El ID del solicitante debe ser un número positivo.")
    private Long solicitanteId;

    @NotBlank(message = "El email del destinatario es obligatorio.")
    @Email(message = "El email del destinatario debe tener un formato válido.")
    private String emailDestinatario;
}
