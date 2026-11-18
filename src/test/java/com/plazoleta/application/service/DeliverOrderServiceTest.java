package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.exception.InvalidOrderStateException;
import com.plazoleta.domain.exception.InvalidPinException;
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
class DeliverOrderServiceTest {

    @Mock private OrderPersistencePort orderPersistencePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private DeliverOrderService service;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new DeliverOrderService(orderPersistencePort, authenticatedUserPort);
        order = new Order(5L, 50L, LocalDateTime.now(), OrderStatus.LISTO, 80L, 3L, "123456",
                List.of(new OrderDish(10L, 2)));
    }

    @Test
    void deliverOrder_conPinCorrecto_pasaAEntregado() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        assertEquals(OrderStatus.ENTREGADO, service.deliverOrder(5L, "123456").getEstado());
    }

    @Test
    void deliverOrder_conPinIncorrecto_lanzaExcepcionYNoCambiaElEstado() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);

        assertThrows(InvalidPinException.class, () -> service.deliverOrder(5L, "000000"));
        verify(orderPersistencePort, never()).save(any());
        assertEquals(OrderStatus.LISTO, order.getEstado());
    }

    @Test
    void deliverOrder_pedidoQueNoEstaListo_lanzaExcepcion() {
        order.setEstado(OrderStatus.EN_PREPARACION);
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);

        assertThrows(InvalidOrderStateException.class, () -> service.deliverOrder(5L, "123456"));
        verify(orderPersistencePort, never()).save(any());
    }

    @Test
    void deliverOrder_pedidoYaEntregado_noSePuedeModificar() {
        order.setEstado(OrderStatus.ENTREGADO);
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);

        assertThrows(InvalidOrderStateException.class, () -> service.deliverOrder(5L, "123456"));
    }

    @Test
    void deliverOrder_empleadoDeOtroRestaurante_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(99L);

        assertThrows(NotRestaurantEmployeeException.class, () -> service.deliverOrder(5L, "123456"));
    }

    @Test
    void deliverOrder_pedidoInexistente_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.deliverOrder(5L, "123456"));
    }
}
