package com.pabei.Despensa.domain.ports;

import com.pabei.Despensa.domain.dto.SolicitudAmistadResponse;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.model.DireccionSolicitudAmistad;
import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;

import java.util.List;

public interface AgregarAmigoPort {
    List<UsuarioResponse> listarAmigos(Long usuarioId);

    List<SolicitudAmistadResponse> listarSolicitudes(
            Long usuarioId,
            EstadoSolicitudAmistad estado,
            DireccionSolicitudAmistad direccion
    );

    SolicitudAmistadResponse enviarSolicitud(Long solicitanteId, String emailDestinatario);

    SolicitudAmistadResponse aceptarSolicitud(Long solicitudId, Long destinatarioId);

    SolicitudAmistadResponse rechazarSolicitud(Long solicitudId, Long destinatarioId);

    void cancelarSolicitud(Long solicitudId, Long solicitanteId);
}
