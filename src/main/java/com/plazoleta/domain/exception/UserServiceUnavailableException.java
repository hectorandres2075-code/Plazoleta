package com.plazoleta.domain.exception;

public class UserServiceUnavailableException extends RuntimeException {

    public UserServiceUnavailableException() {
        super("No se pudo contactar al servicio de usuarios");
    }
}
