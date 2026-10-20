package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Dish;

public interface CreateDishUseCase {

    Dish createDish(Dish dish);
}
