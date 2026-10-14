package com.pabei.Despensa.application.services;

import com.pabei.Despensa.domain.dto.SolicitudAmistadResponse;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadConflictoException;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadInvalidaException;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadNoEncontradaException;
import com.pabei.Despensa.domain.exceptions.UsuarioNoEncontradoException;
import com.pabei.Despensa.domain.model.DireccionSolicitudAmistad;
import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;
import com.pabei.Despensa.domain.model.SolicitudAmistad;
import com.pabei.Despensa.domain.model.Usuario;
import com.pabei.Despensa.domain.ports.AgregarAmigoPort;
import com.pabei.Despensa.domain.repository.SolicitudAmistadRepository;
import com.pabei.Despensa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AgregarAmigoService implements AgregarAmigoPort {

    private final UsuarioRepository usuarioRepository;
    private final SolicitudAmistadRepository solicitudAmistadRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarAmigos(Long usuarioId) {
        return buscarUsuarioPorId(usuarioId).getAmigos().stream()
                .map(this::mapearUsuario)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAmistadResponse> listarSolicitudes(
            Long usuarioId,
            EstadoSolicitudAmistad estado,
            DireccionSolicitudAmistad direccion
    ) {
        buscarUsuarioPorId(usuarioId);
        List<SolicitudAmistad> solicitudes = switch (direccion) {
            case RECIBIDAS -> solicitudAmistadRepository.findAllByDestinatario_UsuarioIdAndEstado(usuarioId, estado);
            case ENVIADAS -> solicitudAmistadRepository.findAllBySolicitante_UsuarioIdAndEstado(usuarioId, estado);
        };
        return solicitudes.stream().map(this::mapearRespuesta).toList();
    }

    @Override
    public SolicitudAmistadResponse enviarSolicitud(Long solicitanteId, String emailDestinatario) {
        Usuario solicitante = buscarUsuarioPorId(solicitanteId);
        Usuario destinatario = usuarioRepository.findByEmail(emailDestinatario)
                .orElseThrow(() -> new UsuarioNoEncontradoException(emailDestinatario));

        if (solicitanteId.equals(destinatario.getUsuarioId())) {
            throw new SolicitudAmistadInvalidaException("No puedes enviarte una solicitud de amistad.");
        }
        if (esAmistadExistente(solicitante, destinatario)) {
            throw new SolicitudAmistadConflictoException("Estos usuarios ya son amigos.");
        }
        if (haySolicitudPendiente(solicitanteId, destinatario.getUsuarioId())
                || haySolicitudPendiente(destinatario.getUsuarioId(), solicitanteId)) {
            throw new SolicitudAmistadConflictoException(
                    "Ya existe una solicitud de amistad pendiente entre estos usuarios."
            );
        }

        SolicitudAmistad solicitud = new SolicitudAmistad();
        solicitud.setSolicitante(solicitante);
        solicitud.setDestinatario(destinatario);
        return mapearRespuesta(solicitudAmistadRepository.save(solicitud));
    }

    @Override
    public SolicitudAmistadResponse aceptarSolicitud(Long solicitudId, Long destinatarioId) {
        SolicitudAmistad solicitud = buscarSolicitudPendiente(solicitudId, destinatarioId);
        Usuario solicitante = solicitud.getSolicitante();
        Usuario destinatario = solicitud.getDestinatario();

        solicitante.getAmigos().add(destinatario);
        destinatario.getAmigos().add(solicitante);
        usuarioRepository.save(solicitante);
        usuarioRepository.save(destinatario);

        solicitud.setEstado(EstadoSolicitudAmistad.ACEPTADA);
        return mapearRespuesta(solicitudAmistadRepository.save(solicitud));
    }

    @Override
    public SolicitudAmistadResponse rechazarSolicitud(Long solicitudId, Long destinatarioId) {
        SolicitudAmistad solicitud = buscarSolicitudPendiente(solicitudId, destinatarioId);
        solicitud.setEstado(EstadoSolicitudAmistad.RECHAZADA);
        return mapearRespuesta(solicitudAmistadRepository.save(solicitud));
    }

    @Override
    public void cancelarSolicitud(Long solicitudId, Long solicitanteId) {
        SolicitudAmistad solicitud = solicitudAmistadRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudAmistadNoEncontradaException(solicitudId));
        if (!solicitanteId.equals(solicitud.getSolicitante().getUsuarioId())) {
            throw new SolicitudAmistadInvalidaException("Solo el solicitante puede cancelar la solicitud.");
        }
        if (solicitud.getEstado() != EstadoSolicitudAmistad.PENDIENTE) {
            throw new SolicitudAmistadConflictoException("Solo se pueden cancelar solicitudes pendientes.");
        }
        solicitudAmistadRepository.delete(solicitud);
    }

    private Usuario buscarUsuarioPorId(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId.toString()));
    }

    private SolicitudAmistad buscarSolicitudPendiente(Long solicitudId, Long destinatarioId) {
        SolicitudAmistad solicitud = solicitudAmistadRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudAmistadNoEncontradaException(solicitudId));

        if (!destinatarioId.equals(solicitud.getDestinatario().getUsuarioId())) {
            throw new SolicitudAmistadInvalidaException("Solo el destinatario puede responder a la solicitud.");
        }
        if (solicitud.getEstado() != EstadoSolicitudAmistad.PENDIENTE) {
            throw new SolicitudAmistadConflictoException("La solicitud ya fue respondida.");
        }

        return solicitud;
    }

    private boolean haySolicitudPendiente(Long solicitanteId, Long destinatarioId) {
        return solicitudAmistadRepository.existsBySolicitante_UsuarioIdAndDestinatario_UsuarioIdAndEstado(
                solicitanteId,
                destinatarioId,
                EstadoSolicitudAmistad.PENDIENTE
        );
    }

    private boolean esAmistadExistente(Usuario primerUsuario, Usuario segundoUsuario) {
        return contieneAmigo(primerUsuario, segundoUsuario)
                || contieneAmigo(segundoUsuario, primerUsuario);
    }

    private boolean contieneAmigo(Usuario usuario, Usuario amigo) {
        return usuario.getAmigos().stream()
                .anyMatch(actual -> amigo.getUsuarioId().equals(actual.getUsuarioId()));
    }

    private SolicitudAmistadResponse mapearRespuesta(SolicitudAmistad solicitud) {
        return new SolicitudAmistadResponse(
                solicitud.getSolicitudId(),
                solicitud.getSolicitante().getUsuarioId(),
                solicitud.getDestinatario().getUsuarioId(),
                solicitud.getEstado()
        );
    }

    private UsuarioResponse mapearUsuario(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getUsuarioId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail()
        );
    }
}
