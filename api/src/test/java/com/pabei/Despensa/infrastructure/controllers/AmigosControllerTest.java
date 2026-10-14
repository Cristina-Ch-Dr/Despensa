package com.pabei.Despensa.infrastructure.controllers;

import com.pabei.Despensa.domain.dto.SolicitudAmistadResponse;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.model.DireccionSolicitudAmistad;
import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;
import com.pabei.Despensa.domain.ports.AgregarAmigoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmigosControllerTest {

    @Mock
    private AgregarAmigoPort agregarAmigoPort;

    @InjectMocks
    private AmigosController controller;

    @Test
    void listarAmigosDelegaAlPuerto() {
        List<UsuarioResponse> amigos = List.of(new UsuarioResponse(2L, "Bea", "Lopez", "bea@example.com"));
        when(agregarAmigoPort.listarAmigos(1L)).thenReturn(amigos);

        assertEquals(amigos, controller.listarAmigos(1L));
        verify(agregarAmigoPort).listarAmigos(1L);
    }

    @Test
    void listarSolicitudesDelegaBandejaYEstadoAlPuerto() {
        List<SolicitudAmistadResponse> solicitudes = List.of(
                new SolicitudAmistadResponse(
                        10L,
                        new UsuarioResponse(2L, "Juan", "Pérez", "juan@example.com"),
                        new UsuarioResponse(1L, "Ana", "López", "ana@example.com"),
                        EstadoSolicitudAmistad.PENDIENTE
                )
        );
        when(agregarAmigoPort.listarSolicitudes(
                1L, EstadoSolicitudAmistad.PENDIENTE, DireccionSolicitudAmistad.RECIBIDAS
        )).thenReturn(solicitudes);

        assertEquals(
                solicitudes,
                controller.listarSolicitudes(1L, EstadoSolicitudAmistad.PENDIENTE, DireccionSolicitudAmistad.RECIBIDAS)
        );
        verify(agregarAmigoPort).listarSolicitudes(
                1L, EstadoSolicitudAmistad.PENDIENTE, DireccionSolicitudAmistad.RECIBIDAS
        );
    }

}
