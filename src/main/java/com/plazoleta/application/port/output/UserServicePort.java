package com.plazoleta.application.port.output;

import java.util.Optional;

/** Lo que Plazoleta necesita del microservicio de Usuarios. */
public interface UserServicePort {

    /** Devuelve el rol del usuario (ej. "PROPIETARIO") o vacío si no existe. */
    Optional<String> findRoleById(Long userId);
}
