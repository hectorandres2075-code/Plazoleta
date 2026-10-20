package com.plazoleta.application.port.output;

import com.plazoleta.domain.model.PageResult;
import com.plazoleta.domain.model.Restaurant;

import java.util.Optional;

public interface RestaurantPersistencePort {

    boolean existsByNit(String nit);

    Optional<Restaurant> findById(Long id);

    /** Restaurantes ordenados por nombre, paginados. */
    PageResult<Restaurant> findAllOrderedByName(int page, int size);

    Restaurant save(Restaurant restaurant);
}
