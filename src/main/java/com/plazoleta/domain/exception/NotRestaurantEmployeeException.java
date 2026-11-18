package com.plazoleta.domain.exception;

public class NotRestaurantEmployeeException extends RuntimeException {

    public NotRestaurantEmployeeException() {
        super("Solo un empleado del restaurante puede gestionar sus pedidos");
    }
}
