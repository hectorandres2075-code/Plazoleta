package com.plazoleta.application.service;

import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.application.port.output.UserServicePort;
import com.plazoleta.domain.exception.InvalidRestaurantException;
import com.plazoleta.domain.exception.RestaurantAlreadyExistsException;
import com.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantServiceTest {

    @Mock private RestaurantPersistencePort restaurantPersistencePort;
    @Mock private UserServicePort userServicePort;

    private CreateRestaurantService service;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        service = new CreateRestaurantService(restaurantPersistencePort, userServicePort);
        restaurant = new Restaurant(null, "Sabor Criollo", "900123456", "Calle 1 # 2-3",
                "+573001112233", "http://logo.com/a.png", 7L);
    }

    @Test
    void createRestaurant_datosValidos_guardaRestaurante() {
        when(userServicePort.findRoleById(7L)).thenReturn(Optional.of("PROPIETARIO"));
        when(restaurantPersistencePort.save(any(Restaurant.class))).thenAnswer(i -> i.getArgument(0));

        Restaurant result = service.createRestaurant(restaurant);

        assertEquals("Sabor Criollo", result.getNombre());
        verify(restaurantPersistencePort).save(restaurant);
    }

    @Test
    void createRestaurant_nombreSoloNumerico_lanzaExcepcion() {
        restaurant.setNombre("12345");

        assertThrows(InvalidRestaurantException.class, () -> service.createRestaurant(restaurant));
        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_propietarioInexistente_lanzaExcepcion() {
        when(userServicePort.findRoleById(7L)).thenReturn(Optional.empty());

        assertThrows(InvalidRestaurantException.class, () -> service.createRestaurant(restaurant));
        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_usuarioSinRolPropietario_lanzaExcepcion() {
        when(userServicePort.findRoleById(7L)).thenReturn(Optional.of("CLIENTE"));

        assertThrows(InvalidRestaurantException.class, () -> service.createRestaurant(restaurant));
        verify(restaurantPersistencePort, never()).save(any());
    }

    @Test
    void createRestaurant_nitRepetido_lanzaExcepcion() {
        when(userServicePort.findRoleById(7L)).thenReturn(Optional.of("PROPIETARIO"));
        when(restaurantPersistencePort.existsByNit("900123456")).thenReturn(true);

        assertThrows(RestaurantAlreadyExistsException.class, () -> service.createRestaurant(restaurant));
        verify(restaurantPersistencePort, never()).save(any());
    }
}
