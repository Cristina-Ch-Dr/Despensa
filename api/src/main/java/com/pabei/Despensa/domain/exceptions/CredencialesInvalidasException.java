package com.pabei.Despensa.domain.exceptions;

public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException() {
        super("Credenciales inválidas");
    }
}
