package com.plazoleta.application.port.output;

import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.PageResult;

import java.util.Optional;

public interface DishPersistencePort {

    Optional<Dish> findById(Long id);

    /** Platos activos del restaurante ordenados por categoría y nombre. categoria puede ser null. */
    PageResult<Dish> findActiveByRestaurant(Long restaurantId, String categoria, int page, int size);

    Dish save(Dish dish);
}
