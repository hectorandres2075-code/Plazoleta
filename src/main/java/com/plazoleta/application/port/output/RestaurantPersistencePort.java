package com.plazoleta.application.port.output;

import com.plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface RestaurantPersistencePort {

    boolean existsByNit(String nit);

    Optional<Restaurant> findById(Long id);

    Restaurant save(Restaurant restaurant);
}