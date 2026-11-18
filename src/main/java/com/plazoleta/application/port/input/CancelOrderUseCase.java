package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Order;

public interface CancelOrderUseCase {

    /** El cliente cancela su propio pedido, solo si sigue PENDIENTE. */
    Order cancelOrder(Long orderId);
}
