package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.PageResult;

public interface ListDishesUseCase {

    /**
     * Lista los platos ACTIVOS de un restaurante, agrupados por categoría (y por nombre dentro de cada una).
     * Si categoria es null o vacía, devuelve todas las categorías.
     */
    PageResult<Dish> listDishes(Long restaurantId, String categoria, int page, int size);
}
