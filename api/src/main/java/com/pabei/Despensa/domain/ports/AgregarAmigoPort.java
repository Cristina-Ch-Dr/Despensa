package com.pabei.Despensa.domain.ports;

import com.pabei.Despensa.domain.dto.SolicitudAmistadResponse;

public interface AgregarAmigoPort {
    SolicitudAmistadResponse enviarSolicitud(Long solicitanteId, String emailDestinatario);

    SolicitudAmistadResponse aceptarSolicitud(Long solicitudId, Long destinatarioId);

    SolicitudAmistadResponse rechazarSolicitud(Long solicitudId, Long destinatarioId);
}
