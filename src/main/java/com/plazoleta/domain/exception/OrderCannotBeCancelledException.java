package com.plazoleta.domain.exception;

public class OrderCannotBeCancelledException extends RuntimeException {

    public OrderCannotBeCancelledException() {
        super("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
    }
}
