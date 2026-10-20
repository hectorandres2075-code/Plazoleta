package com.plazoleta.domain.exception;

public class InvalidRestaurantException extends RuntimeException {

    public InvalidRestaurantException(String message) {
        super(message);
    }
}
