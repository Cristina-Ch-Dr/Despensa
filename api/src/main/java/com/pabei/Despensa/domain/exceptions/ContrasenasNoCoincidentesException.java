package com.pabei.Despensa.domain.exceptions;

public class ContrasenasNoCoincidentesException extends RuntimeException {
    public ContrasenasNoCoincidentesException() {
        super("Las contraseñas no coinciden");
    }
}
