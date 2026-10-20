package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.DishNotFoundException;
import com.plazoleta.domain.exception.InvalidDishException;
import com.plazoleta.domain.exception.NotRestaurantOwnerException;
import com.plazoleta.domain.model.Dish;
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
class UpdateDishServiceTest {

    @Mock private DishPersistencePort dishPersistencePort;
    @Mock private RestaurantPersistencePort restaurantPersistencePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private UpdateDishService service;
    private Dish dish;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        service = new UpdateDishService(dishPersistencePort, restaurantPersistencePort, authenticatedUserPort);
        dish = new Dish(10L, "Bandeja paisa", 25000, "Plato típico", "http://img.com/a.png", "Almuerzos", true, 1L);
        restaurant = new Restaurant(1L, "Sabor Criollo", "900123456", "Calle 1", "+573001112233", "http://logo", 7L);
    }

    @Test
    void updateDish_propietarioDelRestaurante_actualizaPrecioYDescripcion() {
        when(dishPersistencePort.findById(10L)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.currentUserId()).thenReturn(7L);
        when(dishPersistencePort.save(any(Dish.class))).thenAnswer(i -> i.getArgument(0));

        Dish result = service.updateDish(10L, 30000, "Nueva descripción");

        assertEquals(30000, result.getPrecio());
        assertEquals("Nueva descripción", result.getDescripcion());
        assertEquals("Bandeja paisa", result.getNombre()); // el nombre no cambia
    }

    @Test
    void updateDish_platoInexistente_lanzaExcepcion() {
        when(dishPersistencePort.findById(10L)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> service.updateDish(10L, 30000, "x"));
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateDish_propietarioDeOtroRestaurante_lanzaExcepcion() {
        when(dishPersistencePort.findById(10L)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.currentUserId()).thenReturn(99L);

        assertThrows(NotRestaurantOwnerException.class, () -> service.updateDish(10L, 30000, "x"));
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateDish_precioInvalido_lanzaExcepcion() {
        assertThrows(InvalidDishException.class, () -> service.updateDish(10L, 0, "x"));
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateDish_descripcionVacia_lanzaExcepcion() {
        assertThrows(InvalidDishException.class, () -> service.updateDish(10L, 30000, " "));
        verify(dishPersistencePort, never()).save(any());
    }
}
