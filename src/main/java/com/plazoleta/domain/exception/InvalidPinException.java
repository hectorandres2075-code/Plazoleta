package com.plazoleta.domain.exception;

public class InvalidPinException extends RuntimeException {

    public InvalidPinException() {
        super("El PIN de seguridad no es correcto");
    }
}
