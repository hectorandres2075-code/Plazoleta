package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Dish;

public interface UpdateDishStatusUseCase {

    /** Habilita (true) o deshabilita (false) un plato del menú. */
    Dish updateStatus(Long dishId, boolean activo);
}
