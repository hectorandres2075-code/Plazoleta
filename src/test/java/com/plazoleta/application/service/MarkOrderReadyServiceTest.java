package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.MessagingServicePort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.application.port.output.UserServicePort;
import com.plazoleta.domain.exception.InvalidOrderException;
import com.plazoleta.domain.exception.InvalidOrderStateException;
import com.plazoleta.domain.exception.MessagingServiceUnavailableException;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarkOrderReadyServiceTest {

    @Mock private OrderPersistencePort orderPersistencePort;
    @Mock private UserServicePort userServicePort;
    @Mock private MessagingServicePort messagingServicePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private MarkOrderReadyService service;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new MarkOrderReadyService(orderPersistencePort, userServicePort,
                messagingServicePort, authenticatedUserPort);
        order = new Order(5L, 50L, LocalDateTime.now(), OrderStatus.EN_PREPARACION, 80L, 3L, null,
                List.of(new OrderDish(10L, 2)));
    }

    @Test
    void markReady_pedidoEnPreparacion_generaPinEnviaSmsYPasaAListo() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);
        when(userServicePort.findPhoneById(50L)).thenReturn(Optional.of("+573001234567"));
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = service.markReady(5L);

        assertEquals(OrderStatus.LISTO, result.getEstado());
        assertTrue(result.getPin().matches("\\d{6}"));
        verify(messagingServicePort).sendSms(eq("+573001234567"), contains(result.getPin()));
    }

    @Test
    void markReady_pedidoInexistente_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> service.markReady(5L));
    }

    @Test
    void markReady_empleadoDeOtroRestaurante_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(99L);

        assertThrows(NotRestaurantEmployeeException.class, () -> service.markReady(5L));
        verify(messagingServicePort, never()).sendSms(anyString(), anyString());
    }

    @Test
    void markReady_pedidoQueNoEstaEnPreparacion_lanzaExcepcion() {
        order.setEstado(OrderStatus.PENDIENTE);
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);

        assertThrows(InvalidOrderStateException.class, () -> service.markReady(5L));
        verify(messagingServicePort, never()).sendSms(anyString(), anyString());
    }

    @Test
    void markReady_clienteSinCelular_lanzaExcepcion() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);
        when(userServicePort.findPhoneById(50L)).thenReturn(Optional.empty());

        assertThrows(InvalidOrderException.class, () -> service.markReady(5L));
        verify(messagingServicePort, never()).sendSms(anyString(), anyString());
    }

    @Test
    void markReady_siFallaElSms_elPedidoNoCambiaDeEstado() {
        when(orderPersistencePort.findById(5L)).thenReturn(Optional.of(order));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);
        when(userServicePort.findPhoneById(50L)).thenReturn(Optional.of("+573001234567"));
        doThrow(new MessagingServiceUnavailableException()).when(messagingServicePort).sendSms(anyString(), anyString());

        assertThrows(MessagingServiceUnavailableException.class, () -> service.markReady(5L));
        verify(orderPersistencePort, never()).save(any());
        assertEquals(OrderStatus.EN_PREPARACION, order.getEstado());
    }
}
