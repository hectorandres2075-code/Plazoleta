package com.plazoleta.application.service;

import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRestaurantServiceTest {

    @Mock private RestaurantPersistencePort restaurantPersistencePort;
    @InjectMocks private GetRestaurantService service;

    @Test
    void getRestaurant_existente_loDevuelve() {
        Restaurant r = new Restaurant(1L, "Sabor Criollo", "900123456", "Calle 1", "+573001112233", "http://logo", 7L);
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(r));

        assertEquals(7L, service.getRestaurant(1L).getIdPropietario());
    }

    @Test
    void getRestaurant_inexistente_lanzaExcepcion() {
        when(restaurantPersistencePort.findById(9L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> service.getRestaurant(9L));
    }
}
