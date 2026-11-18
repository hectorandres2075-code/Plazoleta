package com.plazoleta.application.port.output;

import com.plazoleta.domain.model.Order;

public interface OrderPersistencePort {

    /** Crea el pedido si no tiene id; si ya tiene, actualiza su estado, chef y pin. */
    Order save(Order order);

    /** ¿Tiene el cliente un pedido en PENDIENTE, EN_PREPARACION o LISTO? */
    boolean existsActiveByClient(Long idCliente);
}
