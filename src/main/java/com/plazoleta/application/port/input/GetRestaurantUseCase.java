package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Restaurant;

public interface GetRestaurantUseCase {

    Restaurant getRestaurant(Long id);
}
