package com.plazoleta.application.service;

import com.plazoleta.application.port.input.CreateDishUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.InvalidDishException;
import com.plazoleta.domain.exception.NotRestaurantOwnerException;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.Restaurant;

public class CreateDishService implements CreateDishUseCase {

    private final DishPersistencePort dishPersistencePort;
    private final RestaurantPersistencePort restaurantPersistencePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public CreateDishService(DishPersistencePort dishPersistencePort,
                             RestaurantPersistencePort restaurantPersistencePort,
                             AuthenticatedUserPort authenticatedUserPort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Dish createDish(Dish dish) {
        if (dish.getPrecio() == null || dish.getPrecio() <= 0) {
            throw new InvalidDishException("El precio debe ser un número entero mayor a 0");
        }

        Restaurant restaurant = restaurantPersistencePort.findById(dish.getIdRestaurante())
                .orElseThrow(() -> new RestaurantNotFoundException(dish.getIdRestaurante()));

        // Un propietario solo puede crear platos en SU restaurante.
        if (!restaurant.getIdPropietario().equals(authenticatedUserPort.currentUserId())) {
            throw new NotRestaurantOwnerException();
        }

        dish.setActivo(true); // todo plato nuevo nace activo
        return dishPersistencePort.save(dish);
    }
}
