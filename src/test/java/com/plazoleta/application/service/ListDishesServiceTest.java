package com.plazoleta.application.service;

import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.InvalidPaginationException;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.PageResult;
import com.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListDishesServiceTest {

    @Mock private DishPersistencePort dishPersistencePort;
    @Mock private RestaurantPersistencePort restaurantPersistencePort;

    private ListDishesService service;
    private Restaurant restaurant;
    private PageResult<Dish> page;

    @BeforeEach
    void setUp() {
        service = new ListDishesService(dishPersistencePort, restaurantPersistencePort);
        restaurant = new Restaurant(1L, "Sabor Criollo", "900123456", "Calle 1", "+573001112233", "http://logo", 7L);
        Dish d = new Dish(10L, "Bandeja paisa", 25000, "Plato típico", "http://img", "Almuerzos", true, 1L);
        page = new PageResult<>(List.of(d), 0, 10, 1, 1);
    }

    @Test
    void listDishes_sinCategoria_pasaFiltroNulo() {
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findActiveByRestaurant(1L, null, 0, 10)).thenReturn(page);

        assertEquals(1, service.listDishes(1L, "  ", 0, 10).getContent().size());
    }

    @Test
    void listDishes_conCategoria_filtraPorEsaCategoria() {
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findActiveByRestaurant(1L, "Almuerzos", 0, 10)).thenReturn(page);

        assertEquals("Almuerzos", service.listDishes(1L, " Almuerzos ", 0, 10).getContent().get(0).getCategoria());
    }

    @Test
    void listDishes_restauranteInexistente_lanzaExcepcion() {
        when(restaurantPersistencePort.findById(9L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class, () -> service.listDishes(9L, null, 0, 10));
        verify(dishPersistencePort, never()).findActiveByRestaurant(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void listDishes_paginaNegativa_lanzaExcepcion() {
        assertThrows(InvalidPaginationException.class, () -> service.listDishes(1L, null, -1, 10));
    }

    @Test
    void listDishes_tamanoInvalido_lanzaExcepcion() {
        assertThrows(InvalidPaginationException.class, () -> service.listDishes(1L, null, 0, 0));
        assertThrows(InvalidPaginationException.class, () -> service.listDishes(1L, null, 0, 999));
    }
}
