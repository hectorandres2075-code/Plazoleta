package com.plazoleta.application.service;

import com.plazoleta.application.port.input.ListDishesUseCase;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.InvalidPaginationException;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.PageResult;

public class ListDishesService implements ListDishesUseCase {

    private static final int TAMANO_MAXIMO = 50;

    private final DishPersistencePort dishPersistencePort;
    private final RestaurantPersistencePort restaurantPersistencePort;

    public ListDishesService(DishPersistencePort dishPersistencePort,
                             RestaurantPersistencePort restaurantPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public PageResult<Dish> listDishes(Long restaurantId, String categoria, int page, int size) {
        if (page < 0) {
            throw new InvalidPaginationException("La página no puede ser negativa");
        }
        if (size < 1 || size > TAMANO_MAXIMO) {
            throw new InvalidPaginationException("El tamaño debe estar entre 1 y " + TAMANO_MAXIMO);
        }
        restaurantPersistencePort.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        String filtro = (categoria == null || categoria.isBlank()) ? null : categoria.trim();
        return dishPersistencePort.findActiveByRestaurant(restaurantId, filtro, page, size);
    }
}
