package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.DishNotFoundException;
import com.plazoleta.domain.exception.NotRestaurantOwnerException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateDishStatusServiceTest {

    @Mock private DishPersistencePort dishPersistencePort;
    @Mock private RestaurantPersistencePort restaurantPersistencePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private UpdateDishStatusService service;
    private Dish dish;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        service = new UpdateDishStatusService(dishPersistencePort, restaurantPersistencePort, authenticatedUserPort);
        dish = new Dish(10L, "Bandeja paisa", 25000, "Plato típico", "http://img", "Almuerzos", true, 1L);
        restaurant = new Restaurant(1L, "Sabor Criollo", "900123456", "Calle 1", "+573001112233", "http://logo", 7L);
    }

    @Test
    void updateStatus_propietarioDelRestaurante_deshabilitaElPlato() {
        when(dishPersistencePort.findById(10L)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.currentUserId()).thenReturn(7L);
        when(dishPersistencePort.save(any(Dish.class))).thenAnswer(i -> i.getArgument(0));

        assertFalse(service.updateStatus(10L, false).isActivo());
    }

    @Test
    void updateStatus_platoDeshabilitado_sePuedeVolverAHabilitar() {
        dish.setActivo(false);
        when(dishPersistencePort.findById(10L)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.currentUserId()).thenReturn(7L);
        when(dishPersistencePort.save(any(Dish.class))).thenAnswer(i -> i.getArgument(0));

        assertTrue(service.updateStatus(10L, true).isActivo());
    }

    @Test
    void updateStatus_platoInexistente_lanzaExcepcion() {
        when(dishPersistencePort.findById(10L)).thenReturn(Optional.empty());

        assertThrows(DishNotFoundException.class, () -> service.updateStatus(10L, false));
        verify(dishPersistencePort, never()).save(any());
    }

    @Test
    void updateStatus_propietarioDeOtroRestaurante_lanzaExcepcion() {
        when(dishPersistencePort.findById(10L)).thenReturn(Optional.of(dish));
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(authenticatedUserPort.currentUserId()).thenReturn(99L);

        assertThrows(NotRestaurantOwnerException.class, () -> service.updateStatus(10L, false));
        verify(dishPersistencePort, never()).save(any());
    }
}
