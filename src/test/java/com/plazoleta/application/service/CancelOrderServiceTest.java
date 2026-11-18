package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.exception.NotOrderOwnerException;
import com.plazoleta.domain.exception.OrderCannotBeCancelledException;
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
class CancelOrderServiceTest {

    @Mock private OrderPersistencePort orderPersistencePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private CancelOrderService service;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new CancelOrderService(orderPersistencePort, authenticatedUserPort);
        order = new Order(5L, 50L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 3L, null,
                List.of(new OrderDish(10L, 2)));
    }

    @Test
    void cancelOrder_pedidoPendienteDelCliente_pasaACancelado() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(OrderStatus.CANCELADO, service.cancelOrder(5L).getEstado());
    }

    @Test
    void cancelOrder_pedidoYaEnPreparacion_lanzaExcepcionConElMensajeDelEnunciado() {
        order.setEstado(OrderStatus.EN_PREPARACION);
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserId()).thenReturn(50L);

        OrderCannotBeCancelledException ex =
                assertThrows(OrderCannotBeCancelledException.class, () -> service.cancelOrder(5L));

        assertEquals("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse", ex.getMessage());
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void cancelOrder_pedidoDeOtroCliente_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserId()).thenReturn(99L);

        assertThrows(NotOrderOwnerException.class, () -> service.cancelOrder(5L));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void cancelOrder_pedidoInexistente_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.cancelOrder(5L));
    }
}
