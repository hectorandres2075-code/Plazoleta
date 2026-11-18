package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Order;

public interface DeliverOrderUseCase {

    /** Marca el pedido como ENTREGADO si el PIN coincide con el que recibió el cliente. */
    Order deliverOrder(Long orderId, String pin);
}
