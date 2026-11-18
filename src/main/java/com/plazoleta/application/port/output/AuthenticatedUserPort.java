package com.plazoleta.application.port.output;

/** Datos del usuario que hizo la petición (salen del token JWT). */
public interface AuthenticatedUserPort {

    Long currentUserId();

    /** Restaurante donde trabaja el empleado, o null si el usuario no es un empleado. */
    Long currentUserRestaurantId();
}
