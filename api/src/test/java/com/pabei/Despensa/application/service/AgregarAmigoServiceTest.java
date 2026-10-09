package com.pabei.Despensa.application.service;

import com.pabei.Despensa.application.services.AgregarAmigoService;
import com.pabei.Despensa.domain.dto.SolicitudAmistadResponse;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadInvalidaException;
import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;
import com.pabei.Despensa.domain.model.SolicitudAmistad;
import com.pabei.Despensa.domain.model.Usuario;
import com.pabei.Despensa.domain.repository.SolicitudAmistadRepository;
import com.pabei.Despensa.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgregarAmigoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SolicitudAmistadRepository solicitudAmistadRepository;

    @InjectMocks
    private AgregarAmigoService agregarAmigoService;

    @Test
    void enviarSolicitudCuandoUsuariosExistenGuardaSolicitudPendiente() {
        Usuario solicitante = usuario(1L, "ana@example.com");
        Usuario destinatario = usuario(2L, "bea@example.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(solicitante));
        when(usuarioRepository.findByEmail("bea@example.com")).thenReturn(Optional.of(destinatario));
        when(solicitudAmistadRepository.existsBySolicitante_UsuarioIdAndDestinatario_UsuarioIdAndEstado(
                any(), any(), eq(EstadoSolicitudAmistad.PENDIENTE)
        )).thenReturn(false);
        when(solicitudAmistadRepository.save(any(SolicitudAmistad.class))).thenAnswer(invocation -> {
            SolicitudAmistad solicitud = invocation.getArgument(0);
            solicitud.setSolicitudId(10L);
            return solicitud;
        });

        SolicitudAmistadResponse response = agregarAmigoService.enviarSolicitud(1L, "bea@example.com");

        assertEquals(new SolicitudAmistadResponse(10L, 1L, 2L, EstadoSolicitudAmistad.PENDIENTE), response);
        verify(solicitudAmistadRepository, times(2))
                .existsBySolicitante_UsuarioIdAndDestinatario_UsuarioIdAndEstado(
                        any(), any(), eq(EstadoSolicitudAmistad.PENDIENTE)
                );
        verify(solicitudAmistadRepository).save(any(SolicitudAmistad.class));
    }

    @Test
    void enviarSolicitudAUnMismoUsuarioLanzaExcepcionSinGuardar() {
        Usuario usuario = usuario(1L, "ana@example.com");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(usuario));

        assertThrows(
                SolicitudAmistadInvalidaException.class,
                () -> agregarAmigoService.enviarSolicitud(1L, "ana@example.com")
        );

        verifyNoInteractions(solicitudAmistadRepository);
    }

    @Test
    void aceptarSolicitudCreaRelacionEnAmbosSentidosYActualizaEstado() {
        Usuario solicitante = usuario(1L, "ana@example.com");
        Usuario destinatario = usuario(2L, "bea@example.com");
        SolicitudAmistad solicitud = solicitud(10L, solicitante, destinatario);
        when(solicitudAmistadRepository.findById(10L)).thenReturn(Optional.of(solicitud));
        when(solicitudAmistadRepository.save(solicitud)).thenReturn(solicitud);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SolicitudAmistadResponse response = agregarAmigoService.aceptarSolicitud(10L, 2L);

        assertTrue(solicitante.getAmigos().contains(destinatario));
        assertTrue(destinatario.getAmigos().contains(solicitante));
        assertEquals(EstadoSolicitudAmistad.ACEPTADA, response.getEstado());
        verify(usuarioRepository, times(2)).save(any(Usuario.class));
        verify(solicitudAmistadRepository).save(solicitud);
    }

    @Test
    void rechazarSolicitudActualizaEstadoSinAgregarAmistad() {
        Usuario solicitante = usuario(1L, "ana@example.com");
        Usuario destinatario = usuario(2L, "bea@example.com");
        SolicitudAmistad solicitud = solicitud(10L, solicitante, destinatario);
        when(solicitudAmistadRepository.findById(10L)).thenReturn(Optional.of(solicitud));
        when(solicitudAmistadRepository.save(solicitud)).thenReturn(solicitud);

        SolicitudAmistadResponse response = agregarAmigoService.rechazarSolicitud(10L, 2L);

        assertEquals(EstadoSolicitudAmistad.RECHAZADA, response.getEstado());
        assertTrue(solicitante.getAmigos().isEmpty());
        assertTrue(destinatario.getAmigos().isEmpty());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void otroUsuarioNoPuedeResponderLaSolicitud() {
        SolicitudAmistad solicitud = solicitud(
                10L,
                usuario(1L, "ana@example.com"),
                usuario(2L, "bea@example.com")
        );
        when(solicitudAmistadRepository.findById(10L)).thenReturn(Optional.of(solicitud));

        assertThrows(
                SolicitudAmistadInvalidaException.class,
                () -> agregarAmigoService.aceptarSolicitud(10L, 3L)
        );

        verify(solicitudAmistadRepository, never()).save(any(SolicitudAmistad.class));
        verifyNoInteractions(usuarioRepository);
    }

    private Usuario usuario(Long id, String email) {
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(id);
        usuario.setEmail(email);
        return usuario;
    }

    private SolicitudAmistad solicitud(Long id, Usuario solicitante, Usuario destinatario) {
        SolicitudAmistad solicitud = new SolicitudAmistad();
        solicitud.setSolicitudId(id);
        solicitud.setSolicitante(solicitante);
        solicitud.setDestinatario(destinatario);
        return solicitud;
    }
}
