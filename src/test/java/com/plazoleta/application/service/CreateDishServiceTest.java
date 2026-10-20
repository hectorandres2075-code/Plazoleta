package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.InvalidDishException;
import com.plazoleta.domain.exception.NotRestaurantOwnerException;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateDishServiceTest {

    @Mock private DishPersistencePort dishPersistencePort;
    @Mock private RestaurantPersistencePort restaurantPersistencePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private CreateDishService service;
    private Dish dish;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        service = new CreateDishService(dishPersistencePort, restaurantPersistencePort, authenticatedUserPort);
        dish = new Dish(null, "Bandeja paisa", 25000, "Plato típico", "http://img.com/a.png", "Almuerzos", false, 1L);
        restaurant = new Restaurant(1L, "Sabor Criollo", "900123456", "Calle 1", "+573001112233", "http://logo", 7L);
    }

    @Test
    void createDish_propietarioDelRestaurante_guardaPlatoActivo() {
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.currentUserId()).thenReturn(7L);
        when(dishPersistencePort.save(any(Dish.class))).thenAnswer(i -> i.getArgument(0));

        Dish result = service.createDish(dish);

        assertTrue(result.isActivo());
        verify(dishPersistencePort).save(dish);
    }

    @Test
    void createDish_precioCero_lanzaExcepcion() {
        dish.setPrecio(0);

        assertThrows(InvalidDishException.class, () -> service.createDish(dish));
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_restauranteInexistente_lanzaExcepcion() {
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> service.createDish(dish));
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void createDish_propietarioDeOtroRestaurante_lanzaExcepcion() {
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.currentUserId()).thenReturn(99L);

        assertThrows(NotRestaurantOwnerException.class, () -> service.createDish(dish));
        verify(dishPersistencePort, never()).save(any());
    }
}
