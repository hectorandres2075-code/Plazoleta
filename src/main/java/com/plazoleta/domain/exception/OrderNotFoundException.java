package com.plazoleta.domain.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("No existe un pedido con id " + id);
    }
}
