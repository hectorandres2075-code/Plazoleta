package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.ActiveOrderExistsException;
import com.plazoleta.domain.exception.InvalidOrderException;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderDish;
import com.plazoleta.domain.model.OrderStatus;
import com.plazoleta.domain.model.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderServiceTest {

    @Mock private OrderPersistencePort orderPersistencePort;
    @Mock private RestaurantPersistencePort restaurantPersistencePort;
    @Mock private DishPersistencePort dishPersistencePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private CreateOrderService service;
    private Restaurant restaurant;
    private Dish dish1;
    private Dish dish2;

    @BeforeEach
    void setUp() {
        service = new CreateOrderService(orderPersistencePort, restaurantPersistencePort,
                dishPersistencePort, authenticatedUserPort);
        restaurant = new Restaurant(1L, "Sabor Criollo", "900123456", "Calle 1", "+573001112233", "http://logo", 7L);
        dish1 = new Dish(10L, "Bandeja paisa", 25000, "Plato típico", "http://img", "Almuerzos", true, 1L);
        dish2 = new Dish(11L, "Limonada", 5000, "Fría", "http://img", "Bebidas", true, 1L);
    }

    @Test
    void createOrder_datosValidos_creaPedidoPendiente() {
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(orderPersistencePort.existsActiveByClient(50L)).thenReturn(false);
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findAllByIds(any())).thenReturn(List.of(dish1, dish2));
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = service.createOrder(1L, List.of(new OrderDish(10L, 2), new OrderDish(11L, 1)));

        assertEquals(OrderStatus.PENDIENTE, result.getEstado());
        assertEquals(50L, result.getIdCliente());
        assertNull(result.getIdChef());
        assertEquals(2, result.getPlatos().size());
    }

    @Test
    void createOrder_platoRepetido_sumaLasCantidades() {
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findAllByIds(any())).thenReturn(List.of(dish1));
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = service.createOrder(1L, List.of(new OrderDish(10L, 2), new OrderDish(10L, 3)));

        assertEquals(1, result.getPlatos().size());
        assertEquals(5, result.getPlatos().get(0).getCantidad());
    }

    @Test
    void createOrder_clienteConPedidoEnProceso_lanzaExcepcion() {
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(orderPersistencePort.existsActiveByClient(50L)).thenReturn(true);

        assertThrows(ActiveOrderExistsException.class,
                () -> service.createOrder(1L, List.of(new OrderDish(10L, 1))));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void createOrder_sinPlatos_lanzaExcepcion() {
        assertThrows(InvalidOrderException.class, () -> service.createOrder(1L, List.of()));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void createOrder_restauranteInexistente_lanzaExcepcion() {
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class,
                () -> service.createOrder(1L, List.of(new OrderDish(10L, 1))));
    }

    @Test
    void createOrder_cantidadCero_lanzaExcepcion() {
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));

        assertThrows(InvalidOrderException.class,
                () -> service.createOrder(1L, List.of(new OrderDish(10L, 0))));
    }

    @Test
    void createOrder_platoInexistente_lanzaExcepcion() {
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findAllByIds(any())).thenReturn(List.of(dish1)); // falta el 11

        assertThrows(InvalidOrderException.class,
                () -> service.createOrder(1L, List.of(new OrderDish(10L, 1), new OrderDish(11L, 1))));
    }

    @Test
    void createOrder_platoDeOtroRestaurante_lanzaExcepcion() {
        dish1.setIdRestaurante(2L);
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findAllByIds(any())).thenReturn(List.of(dish1));

        assertThrows(InvalidOrderException.class,
                () -> service.createOrder(1L, List.of(new OrderDish(10L, 1))));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void createOrder_platoDeshabilitado_lanzaExcepcion() {
        dish1.setActivo(false);
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(restaurantPersistencePort.findById(1L)).thenReturn(Optional.of(restaurant));
        when(dishPersistencePort.findAllByIds(any())).thenReturn(List.of(dish1));

        assertThrows(InvalidOrderException.class,
                () -> service.createOrder(1L, List.of(new OrderDish(10L, 1))));
        verify(orderPersistencePort, never()).save(any());
    }
}
