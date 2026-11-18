package com.plazoleta.application.service;

import com.plazoleta.application.port.input.CancelOrderUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.exception.NotOrderOwnerException;
import com.plazoleta.domain.exception.OrderCannotBeCancelledException;
import com.plazoleta.domain.exception.OrderNotFoundException;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderStatus;

public class CancelOrderService implements CancelOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public CancelOrderService(OrderPersistencePort orderPersistencePort,
                              AuthenticatedUserPort authenticatedUserPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Order cancelOrder(Long orderId) {
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Un cliente solo puede cancelar sus propios pedidos.
        if (!order.getIdCliente().equals(authenticatedUserPort.currentUserId())) {
            throw new NotOrderOwnerException();
        }
        // Y solo mientras estén PENDIENTES.
        if (order.getEstado() != OrderStatus.PENDIENTE) {
            throw new OrderCannotBeCancelledException();
        }

        order.setEstado(OrderStatus.CANCELADO);
        return orderPersistencePort.save(order);
    }
}
