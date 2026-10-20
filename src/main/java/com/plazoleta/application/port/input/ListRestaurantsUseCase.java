package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.PageResult;
import com.plazoleta.domain.model.Restaurant;

public interface ListRestaurantsUseCase {

    /** Lista los restaurantes ordenados alfabéticamente por nombre. La página empieza en 0. */
    PageResult<Restaurant> listRestaurants(int page, int size);
}
