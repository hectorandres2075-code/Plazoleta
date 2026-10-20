package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Restaurant;

public interface CreateRestaurantUseCase {

    Restaurant createRestaurant(Restaurant restaurant);
}
