package com.plazoleta.application.port.output;

import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderStatus;
import com.plazoleta.domain.model.PageResult;

import java.util.Optional;

public interface OrderPersistencePort {

    /** Crea el pedido si no tiene id; si ya tiene, actualiza su estado, chef y pin. */
    Order save(Order order);

    Optional<Order> findById(Long id);

    /** ¿Tiene el cliente un pedido en PENDIENTE, EN_PREPARACION o LISTO? */
    boolean existsActiveByClient(Long idCliente);

    /** Pedidos del restaurante con ese estado, del más antiguo al más reciente. */
    PageResult<Order> findByRestaurantAndStatus(Long idRestaurante, OrderStatus estado, int page, int size);
}
