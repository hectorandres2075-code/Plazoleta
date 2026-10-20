package com.plazoleta.application.port.output;

import com.plazoleta.domain.model.Dish;

import java.util.Optional;

public interface DishPersistencePort {

    Optional<Dish> findById(Long id);

    Dish save(Dish dish);
}
