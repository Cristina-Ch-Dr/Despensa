package com.pabei.Despensa.domain.repository;

import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;
import com.pabei.Despensa.domain.model.SolicitudAmistad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Long> {
    boolean existsBySolicitante_UsuarioIdAndDestinatario_UsuarioIdAndEstado(
            Long solicitanteId,
            Long destinatarioId,
            EstadoSolicitudAmistad estado
    );
}
