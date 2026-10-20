package com.plazoleta.application.port.output;

import com.plazoleta.domain.model.Dish;

public interface DishPersistencePort {

    Dish save(Dish dish);
}
