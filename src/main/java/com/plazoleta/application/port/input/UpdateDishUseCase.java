package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Dish;

public interface UpdateDishUseCase {

    /** Modifica solo el precio y la descripción de un plato. */
    Dish updateDish(Long dishId, Integer precio, String descripcion);
}
