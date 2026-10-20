package com.plazoleta.application.service;

import com.plazoleta.application.port.input.ListRestaurantsUseCase;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.InvalidPaginationException;
import com.plazoleta.domain.model.PageResult;
import com.plazoleta.domain.model.Restaurant;

public class ListRestaurantsService implements ListRestaurantsUseCase {

    private static final int TAMANO_MAXIMO = 50;

    private final RestaurantPersistencePort restaurantPersistencePort;

    public ListRestaurantsService(RestaurantPersistencePort restaurantPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public PageResult<Restaurant> listRestaurants(int page, int size) {
        if (page < 0) {
            throw new InvalidPaginationException("La página no puede ser negativa");
        }
        if (size < 1 || size > TAMANO_MAXIMO) {
            throw new InvalidPaginationException("El tamaño debe estar entre 1 y " + TAMANO_MAXIMO);
        }
        return restaurantPersistencePort.findAllOrderedByName(page, size);
    }
}
