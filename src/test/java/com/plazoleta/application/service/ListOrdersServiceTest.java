package com.plazoleta.application.service;

import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.exception.InvalidOrderException;
import com.plazoleta.domain.exception.InvalidPaginationException;
import com.plazoleta.domain.exception.NotRestaurantEmployeeException;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderDish;
import com.plazoleta.domain.model.OrderStatus;
import com.plazoleta.domain.model.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListOrdersServiceTest {

    @Mock private OrderPersistencePort orderPersistencePort;
    @Mock private AuthenticatedUserPort authenticatedUserPort;

    private ListOrdersService service;

    @BeforeEach
    void setUp() {
        service = new ListOrdersService(orderPersistencePort, authenticatedUserPort);
    }

    @Test
    void listOrders_empleadoDeUnRestaurante_listaSoloLosDeSuRestaurante() {
        Order order = new Order(1L, 50L, LocalDateTime.now(), OrderStatus.PENDIENTE, null, 3L, null,
                List.of(new OrderDish(10L, 2)));
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(3L);
        when(orderPersistencePort.findByRestaurantAndStatus(3L, OrderStatus.PENDIENTE, 0, 10))
                .thenReturn(new PageResult<>(List.of(order), 0, 10, 1, 1));

        PageResult<Order> result = service.listOrders(OrderStatus.PENDIENTE, 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals(3L, result.getContent().get(0).getIdRestaurante());
    }

    @Test
    void listOrders_usuarioSinRestaurante_lanzaExcepcion() {
        when(authenticatedUserPort.currentUserRestaurantId()).thenReturn(null);

        assertThrows(NotRestaurantEmployeeException.class, () -> service.listOrders(OrderStatus.PENDIENTE, 0, 10));
        verify(orderPersistencePort, never()).findByRestaurantAndStatus(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void listOrders_sinEstado_lanzaExcepcion() {
        assertThrows(InvalidOrderException.class, () -> service.listOrders(null, 0, 10));
    }

    @Test
    void listOrders_paginaNegativa_lanzaExcepcion() {
        assertThrows(InvalidPaginationException.class, () -> service.listOrders(OrderStatus.PENDIENTE, -1, 10));
    }

    @Test
    void listOrders_tamanoInvalido_lanzaExcepcion() {
        assertThrows(InvalidPaginationException.class, () -> service.listOrders(OrderStatus.PENDIENTE, 0, 0));
        assertThrows(InvalidPaginationException.class, () -> service.listOrders(OrderStatus.PENDIENTE, 0, 500));
    }
}
