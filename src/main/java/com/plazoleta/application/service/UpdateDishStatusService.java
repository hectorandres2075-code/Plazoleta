package com.plazoleta.application.service;

import com.plazoleta.application.port.input.UpdateDishStatusUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.DishNotFoundException;
import com.plazoleta.domain.exception.NotRestaurantOwnerException;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.Restaurant;

public class UpdateDishStatusService implements UpdateDishStatusUseCase {

    private final DishPersistencePort dishPersistencePort;
    private final RestaurantPersistencePort restaurantPersistencePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public UpdateDishStatusService(DishPersistencePort dishPersistencePort,
                                   RestaurantPersistencePort restaurantPersistencePort,
                                   AuthenticatedUserPort authenticatedUserPort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Dish updateStatus(Long dishId, boolean activo) {
        Dish dish = dishPersistencePort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        Restaurant restaurant = restaurantPersistencePort.findById(dish.getIdRestaurante())
                .orElseThrow(() -> new RestaurantNotFoundException(dish.getIdRestaurante()));

        // No se permite tocar platos de restaurantes que no son del propietario.
        if (!restaurant.getIdPropietario().equals(authenticatedUserPort.currentUserId())) {
            throw new NotRestaurantOwnerException();
        }

        dish.setActivo(activo);
        return dishPersistencePort.save(dish);
    }
}
