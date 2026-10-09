package com.pabei.Despensa.domain.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String identificador) {
        super("No se encontró el usuario: " + identificador);
    }
}
