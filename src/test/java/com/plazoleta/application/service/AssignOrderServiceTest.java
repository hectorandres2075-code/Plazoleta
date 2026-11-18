package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.exception.InvalidOrderStateException;
import com.plazoleta.domain.exception.NotRestaurantEmployeeException;
import com.plazoleta.domain.exception.OrderNotFoundException;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderDish;
import com.plazoleta.domain.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignOrderServiceTest {

    @Mock private OrderPersistencePort orderPersistencePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private AssignOrderService service;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new AssignOrderService(orderPersistencePort, authenticatedUserPort);
        order = new Order(5L, 50L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 3L, null,
                List.of(new OrderDish(10L, 2)));
    }

    @Test
    void assignOrder_empleadoDelRestaurante_pasaAEnPreparacionYQuedaAsignado() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);
        when(authenticatedUserPort.currentUserId()).thenReturn(80L);
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = service.assignOrder(5L);

        assertEquals(OrderStatus.EN_PREPARACION, result.getEstado());
        assertEquals(80L, result.getIdChef());
    }

    @Test
    void assignOrder_pedidoInexistente_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.assignOrder(5L));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void assignOrder_empleadoDeOtroRestaurante_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(99L);

        assertThrows(NotRestaurantEmployeeException.class, () -> service.assignOrder(5L));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void assignOrder_usuarioSinRestaurante_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(null);

        assertThrows(NotRestaurantEmployeeException.class, () -> service.assignOrder(5L));
    }

    @Test
    void assignOrder_pedidoQueNoEstaPendiente_lanzaExcepcion() {
        order.setEstado(OrderStatus.EN_PREPARACION);
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);

        assertThrows(InvalidOrderStateException.class, () -> service.assignOrder(5L));
        verify(orderPersistencePort, never()).save(any());
    }
}
