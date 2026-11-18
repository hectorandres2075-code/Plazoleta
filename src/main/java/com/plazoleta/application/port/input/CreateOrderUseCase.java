package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderDish;

import java.util.List;

public interface CreateOrderUseCase {

    /** Crea un pedido PENDIENTE para el cliente autenticado. */
    Order createOrder(Long idRestaurante, List<OrderDish> platos);
}
