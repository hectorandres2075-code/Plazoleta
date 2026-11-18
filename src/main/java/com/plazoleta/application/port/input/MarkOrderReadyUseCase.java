package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Order;

public interface MarkOrderReadyUseCase {

    /** Marca el pedido como LISTO, genera su PIN y se lo envía al cliente por SMS. */
    Order markReady(Long orderId);
}
