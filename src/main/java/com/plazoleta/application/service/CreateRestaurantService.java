package com.plazoleta.application.service;

import com.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.application.port.output.UserServicePort;
import com.plazoleta.domain.exception.InvalidRestaurantException;
import com.plazoleta.domain.exception.RestaurantAlreadyExistsException;
import com.plazoleta.domain.model.Restaurant;

public class CreateRestaurantService implements CreateRestaurantUseCase {

    private static final String ROL_PROPIETARIO = "PROPIETARIO";

    private final RestaurantPersistencePort restaurantPersistencePort;
    private final UserServicePort userServicePort;

    public CreateRestaurantService(RestaurantPersistencePort restaurantPersistencePort,
                                   UserServicePort userServicePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userServicePort = userServicePort;
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        if (restaurant.getNombre() == null || restaurant.getNombre().chars().allMatch(Character::isDigit)) {
            throw new InvalidRestaurantException("El nombre no puede ser solo numérico");
        }

        String rol = userServicePort.findRoleById(restaurant.getIdPropietario())
                .orElseThrow(() -> new InvalidRestaurantException("El propietario indicado no existe"));
        if (!ROL_PROPIETARIO.equals(rol)) {
            throw new InvalidRestaurantException("El usuario indicado no tiene el rol PROPIETARIO");
        }

        if (restaurantPersistencePort.existsByNit(restaurant.getNit())) {
            throw new RestaurantAlreadyExistsException("Ya existe un restaurante con ese NIT");
        }

        return restaurantPersistencePort.save(restaurant);
    }
}
