package com.plazoleta.application.service;

import com.plazoleta.application.port.input.ListOrdersUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.exception.InvalidOrderException;
import com.plazoleta.domain.exception.InvalidPaginationException;
import com.plazoleta.domain.exception.NotRestaurantEmployeeException;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderStatus;
import com.plazoleta.domain.model.PageResult;

public class ListOrdersService implements ListOrdersUseCase {

    private static final int TAMANO_MAXIMO = 50;

    private final OrderPersistencePort orderPersistencePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public ListOrdersService(OrderPersistencePort orderPersistencePort,
                             AuthenticatedUserPort authenticatedUserPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public PageResult<Order> listOrders(OrderStatus estado, int page, int size) {
        if (estado == null) {
            throw new InvalidOrderException("Debes indicar el estado por el que quieres filtrar");
        }
        if (page < 0) {
            throw new InvalidPaginationException("La página no puede ser negativa");
        }
        if (size < 1 || size > TAMANO_MAXIMO) {
            throw new InvalidPaginationException("El tamaño debe estar entre 1 y " + TAMANO_MAXIMO);
        }

        // Solo se ven los pedidos del restaurante al que pertenece el empleado.
        Long idRestaurante = authenticatedUserPort.currentUserRestaurantId();
        if (idRestaurante == null) {
            throw new NotRestaurantEmployeeException();
        }
        return orderPersistencePort.findByRestaurantAndStatus(idRestaurante, estado, page, size);
    }
}
