package com.plazoleta.application.port.output;

/** Da el id del usuario que hizo la petición (sale del token JWT). */
public interface AuthenticatedUserPort {

    Long currentUserId();
}
