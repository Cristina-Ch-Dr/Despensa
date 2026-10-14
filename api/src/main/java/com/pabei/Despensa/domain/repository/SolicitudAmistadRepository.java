package com.pabei.Despensa.domain.repository;

import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;
import com.pabei.Despensa.domain.model.SolicitudAmistad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Long> {
    boolean existsBySolicitante_UsuarioIdAndDestinatario_UsuarioIdAndEstado(
            Long solicitanteId,
            Long destinatarioId,
            EstadoSolicitudAmistad estado
    );

    List<SolicitudAmistad> findAllByDestinatario_UsuarioIdAndEstado(Long destinatarioId, EstadoSolicitudAmistad estado);

    List<SolicitudAmistad> findAllBySolicitante_UsuarioIdAndEstado(Long solicitanteId, EstadoSolicitudAmistad estado);
}
