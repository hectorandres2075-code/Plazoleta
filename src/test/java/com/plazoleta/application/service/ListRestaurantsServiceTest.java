package com.plazoleta.application.service;

import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.InvalidPaginationException;
import com.plazoleta.domain.model.PageResult;
import com.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRestaurantsServiceTest {

    @Mock private RestaurantPersistencePort restaurantPersistencePort;
    @InjectMocks private ListRestaurantsService service;

    @Test
    void listRestaurants_paginacionValida_devuelveLaPagina() {
        Restaurant r = new Restaurant(1L, "Sabor Criollo", "900123456", "Calle 1", "+573001112233", "http://logo", 7L);
        PageResult<Restaurant> page = new PageResult<>(List.of(r), 0, 10, 1, 1);
        when(restaurantPersistencePort.findAllOrderedByName(0, 10)).thenReturn(page);

        PageResult<Restaurant> result = service.listRestaurants(0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("Sabor Criollo", result.getContent().get(0).getNombre());
    }

    @Test
    void listRestaurants_paginaNegativa_lanzaExcepcion() {
        assertThrows(InvalidPaginationException.class, () -> service.listRestaurants(-1, 10));
        verify(restaurantPersistencePort, never()).findAllOrderedByName(anyInt(), anyInt());
    }

    @Test
    void listRestaurants_tamanoCero_lanzaExcepcion() {
        assertThrows(InvalidPaginationException.class, () -> service.listRestaurants(0, 0));
    }

    @Test
    void listRestaurants_tamanoMuyGrande_lanzaExcepcion() {
        assertThrows(InvalidPaginationException.class, () -> service.listRestaurants(0, 500));
    }
}
