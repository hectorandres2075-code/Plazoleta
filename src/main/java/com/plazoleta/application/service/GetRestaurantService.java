package com.plazoleta.application.service;

import com.plazoleta.application.port.input.GetRestaurantUseCase;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Restaurant;

public class GetRestaurantService implements GetRestaurantUseCase {

    private final RestaurantPersistencePort restaurantPersistencePort;

    public GetRestaurantService(RestaurantPersistencePort restaurantPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public Restaurant getRestaurant(Long id) {
        return restaurantPersistencePort.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }
}
