package com.plazoleta.application.service;

import com.plazoleta.application.port.input.UpdateDishUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.DishNotFoundException;
import com.plazoleta.domain.exception.InvalidDishException;
import com.plazoleta.domain.exception.NotRestaurantOwnerException;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.Restaurant;

public class UpdateDishService implements UpdateDishUseCase {

    private final DishPersistencePort dishPersistencePort;
    private final RestaurantPersistencePort restaurantPersistencePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public UpdateDishService(DishPersistencePort dishPersistencePort,
                             RestaurantPersistencePort restaurantPersistencePort,
                             AuthenticatedUserPort authenticatedUserPort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Dish updateDish(Long dishId, Integer precio, String descripcion) {
        if (precio == null || precio <= 0) {
            throw new InvalidDishException("El precio debe ser un número entero mayor a 0");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new InvalidDishException("La descripción es obligatoria");
        }

        Dish dish = dishPersistencePort.findById(dishId)
                .orElseThrow(() -> new DishNotFoundException(dishId));

        Restaurant restaurant = restaurantPersistencePort.findById(dish.getIdRestaurante())
                .orElseThrow(() -> new RestaurantNotFoundException(dish.getIdRestaurante()));

        // Solo el propietario del restaurante al que pertenece el plato puede modificarlo.
        if (!restaurant.getIdPropietario().equals(authenticatedUserPort.currentUserId())) {
            throw new NotRestaurantOwnerException();
        }

        dish.setPrecio(precio);
        dish.setDescripcion(descripcion);
        return dishPersistencePort.save(dish);
    }
}
