package com.plazoleta.domain.exception;

public class ActiveOrderExistsException extends RuntimeException {

    public ActiveOrderExistsException() {
        super("Ya tienes un pedido en proceso (pendiente, en preparación o listo). Espera a que termine para hacer otro");
    }
}
