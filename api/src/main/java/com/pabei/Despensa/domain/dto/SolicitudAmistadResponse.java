package com.pabei.Despensa.domain.dto;

import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAmistadResponse {
    private Long solicitudId;
    private UsuarioResponse solicitante;
    private UsuarioResponse destinatario;
    private EstadoSolicitudAmistad estado;
}
