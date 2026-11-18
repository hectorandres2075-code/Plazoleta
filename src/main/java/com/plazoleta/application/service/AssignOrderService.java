package com.plazoleta.application.service;

import com.plazoleta.application.port.input.AssignOrderUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.exception.InvalidOrderStateException;
import com.plazoleta.domain.exception.NotRestaurantEmployeeException;
import com.plazoleta.domain.exception.OrderNotFoundException;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderStatus;

public class AssignOrderService implements AssignOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public AssignOrderService(OrderPersistencePort orderPersistencePort,
                              AuthenticatedUserPort authenticatedUserPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Order assignOrder(Long orderId) {
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // Solo un empleado del restaurante dueño del pedido puede asignárselo.
        Long idRestaurante = authenticatedUserPort.currentUserRestaurantId();
        if (idRestaurante == null || !idRestaurante.equals(order.getIdRestaurante())) {
            throw new NotRestaurantEmployeeException();
        }

        if (order.getEstado() != OrderStatus.PENDIENTE) {
            throw new InvalidOrderStateException("Solo se puede tomar un pedido que esté PENDIENTE");
        }

        order.setIdChef(authenticatedUserPort.currentUserId());
        order.setEstado(OrderStatus.EN_PREPARACION);
        return orderPersistencePort.save(order);
    }
}
