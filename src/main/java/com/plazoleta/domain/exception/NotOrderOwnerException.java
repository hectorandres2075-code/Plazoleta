package com.plazoleta.domain.exception;

public class NotOrderOwnerException extends RuntimeException {

    public NotOrderOwnerException() {
        super("Solo puedes cancelar tus propios pedidos");
    }
}
