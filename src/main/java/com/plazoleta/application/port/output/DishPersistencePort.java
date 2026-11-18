package com.plazoleta.application.port.output;

import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DishPersistencePort {

    Optional<Dish> findById(Long id);

    /** Devuelve los platos que existan con esos ids (los que no existan simplemente no vienen). */
    List<Dish> findAllByIds(Collection<Long> ids);

    /** Platos activos del restaurante ordenados por categoría y nombre. categoria puede ser null. */
    PageResult<Dish> findActiveByRestaurant(Long restaurantId, String categoria, int page, int size);

    Dish save(Dish dish);
}
