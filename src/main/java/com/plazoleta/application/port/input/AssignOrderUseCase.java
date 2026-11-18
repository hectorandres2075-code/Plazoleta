package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Order;

public interface AssignOrderUseCase {

    /** El empleado autenticado se asigna el pedido y este pasa a EN_PREPARACION. */
    Order assignOrder(Long orderId);
}
