package com.plazoleta.domain.exception;

public class DishNotFoundException extends RuntimeException {

    public DishNotFoundException(Long id) {
        super("No existe un plato con id " + id);
    }
}
