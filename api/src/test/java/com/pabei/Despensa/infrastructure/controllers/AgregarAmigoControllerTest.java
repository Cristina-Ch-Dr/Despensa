package com.pabei.Despensa.infrastructure.controllers;

import com.pabei.Despensa.domain.dto.EnviarSolicitudAmistadRequest;
import com.pabei.Despensa.domain.dto.ResponderSolicitudAmistadRequest;
import com.pabei.Despensa.domain.dto.SolicitudAmistadResponse;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;
import com.pabei.Despensa.domain.ports.AgregarAmigoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarAmigoControllerTest {

    @Mock
    private AgregarAmigoPort agregarAmigoPort;

    @InjectMocks
    private AgregarAmigoController controller;

    @Test
    void enviarSolicitudDevuelveCreatedYDelegaAlPuerto() {
        EnviarSolicitudAmistadRequest request =
                new EnviarSolicitudAmistadRequest(1L, "bea@example.com");
        SolicitudAmistadResponse response =
                solicitudResponse(EstadoSolicitudAmistad.PENDIENTE);
        when(agregarAmigoPort.enviarSolicitud(1L, "bea@example.com")).thenReturn(response);

        ResponseEntity<SolicitudAmistadResponse> result = controller.enviarSolicitud(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(agregarAmigoPort).enviarSolicitud(1L, "bea@example.com");
    }

    @Test
    void aceptarSolicitudDevuelveOkYDelegaAlPuerto() {
        ResponderSolicitudAmistadRequest request = new ResponderSolicitudAmistadRequest(2L);
        SolicitudAmistadResponse response =
                solicitudResponse(EstadoSolicitudAmistad.ACEPTADA);
        when(agregarAmigoPort.aceptarSolicitud(10L, 2L)).thenReturn(response);

        ResponseEntity<SolicitudAmistadResponse> result = controller.aceptarSolicitud(10L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(agregarAmigoPort).aceptarSolicitud(10L, 2L);
    }

    @Test
    void rechazarSolicitudDevuelveOkYDelegaAlPuerto() {
        ResponderSolicitudAmistadRequest request = new ResponderSolicitudAmistadRequest(2L);
        SolicitudAmistadResponse response =
                solicitudResponse(EstadoSolicitudAmistad.RECHAZADA);
        when(agregarAmigoPort.rechazarSolicitud(10L, 2L)).thenReturn(response);

        ResponseEntity<SolicitudAmistadResponse> result = controller.rechazarSolicitud(10L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(agregarAmigoPort).rechazarSolicitud(10L, 2L);
    }

    @Test
    void cancelarSolicitudDevuelveNoContentYDelegaAlPuerto() {
        ResponseEntity<Void> result = controller.cancelarSolicitud(10L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(agregarAmigoPort).cancelarSolicitud(10L, 1L);
    }

    private SolicitudAmistadResponse solicitudResponse(EstadoSolicitudAmistad estado) {
        return new SolicitudAmistadResponse(
                10L,
                new UsuarioResponse(1L, "Ana", "Lopez", "ana@example.com"),
                new UsuarioResponse(2L, "Bea", "Lopez", "bea@example.com"),
                estado
        );
    }
}
