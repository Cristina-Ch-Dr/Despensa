package com.pabei.Despensa.infrastructure.controllers;

import com.pabei.Despensa.domain.dto.EnviarSolicitudAmistadRequest;
import com.pabei.Despensa.domain.dto.ResponderSolicitudAmistadRequest;
import com.pabei.Despensa.domain.dto.SolicitudAmistadResponse;
import com.pabei.Despensa.domain.ports.AgregarAmigoPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amigos/solicitudes")
@RequiredArgsConstructor
public class AgregarAmigoController {

    private final AgregarAmigoPort agregarAmigoPort;

    @PostMapping
    public ResponseEntity<SolicitudAmistadResponse> enviarSolicitud(
            @Valid @RequestBody EnviarSolicitudAmistadRequest request
    ) {
        SolicitudAmistadResponse response = agregarAmigoPort.enviarSolicitud(
                request.getSolicitanteId(),
                request.getEmailDestinatario()
        );
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/{solicitudId}/aceptar")
    public ResponseEntity<SolicitudAmistadResponse> aceptarSolicitud(
            @PathVariable @Positive(message = "El ID de la solicitud debe ser un número positivo.") Long solicitudId,
            @Valid @RequestBody ResponderSolicitudAmistadRequest request
    ) {
        SolicitudAmistadResponse response = agregarAmigoPort.aceptarSolicitud(
                solicitudId,
                request.getDestinatarioId()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{solicitudId}/rechazar")
    public ResponseEntity<SolicitudAmistadResponse> rechazarSolicitud(
            @PathVariable @Positive(message = "El ID de la solicitud debe ser un número positivo.") Long solicitudId,
            @Valid @RequestBody ResponderSolicitudAmistadRequest request
    ) {
        SolicitudAmistadResponse response = agregarAmigoPort.rechazarSolicitud(
                solicitudId,
                request.getDestinatarioId()
        );
        return ResponseEntity.ok(response);
    }
}
