package com.pabei.Despensa.domain.exceptions;

public class SolicitudAmistadNoEncontradaException extends RuntimeException {
    public SolicitudAmistadNoEncontradaException(Long solicitudId) {
        super("No se encontró la solicitud de amistad con ID " + solicitudId);
    }
}
