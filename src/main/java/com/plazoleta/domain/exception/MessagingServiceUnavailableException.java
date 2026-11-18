package com.plazoleta.domain.exception;

public class MessagingServiceUnavailableException extends RuntimeException {

    public MessagingServiceUnavailableException() {
        super("No se pudo enviar el SMS: el servicio de mensajería no respondió");
    }
}
