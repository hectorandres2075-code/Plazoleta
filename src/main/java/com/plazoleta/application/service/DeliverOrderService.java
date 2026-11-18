package com.plazoleta.application.service;

import com.plazoleta.application.port.input.DeliverOrderUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.exception.InvalidOrderStateException;
import com.plazoleta.domain.exception.InvalidPinException;
import com.plazoleta.domain.exception.NotRestaurantEmployeeException;
import com.plazoleta.domain.exception.OrderNotFoundException;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderStatus;

public class DeliverOrderService implements DeliverOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public DeliverOrderService(OrderPersistencePort orderPersistencePort,
                               AuthenticatedUserPort authenticatedUserPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Order deliverOrder(Long orderId, String pin) {
        Order order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        Long idRestaurante = authenticatedUserPort.currentUserRestaurantId();
        if (idRestaurante == null || !idRestaurante.equals(order.getIdRestaurante())) {
            throw new NotRestaurantEmployeeException();
        }

        // Solo un pedido LISTO se puede entregar; uno ya ENTREGADO no vuelve a cambiar.
        if (order.getEstado() != OrderStatus.LISTO) {
            throw new InvalidOrderStateException("Solo se puede entregar un pedido que esté LISTO");
        }
        if (order.getPin() == null || !order.getPin().equals(pin)) {
            throw new InvalidPinException();
        }

        order.setEstado(OrderStatus.ENTREGADO);
        return orderPersistencePort.save(order);
    }
}
