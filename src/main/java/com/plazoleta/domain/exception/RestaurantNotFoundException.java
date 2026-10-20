package com.plazoleta.domain.exception;

public class RestaurantNotFoundException extends RuntimeException {

    public RestaurantNotFoundException(Long id) {
        super("No existe un restaurante con id " + id);
    }
}
