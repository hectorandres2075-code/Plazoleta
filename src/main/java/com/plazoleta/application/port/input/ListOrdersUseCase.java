package com.plazoleta.application.port.input;

import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderStatus;
import com.plazoleta.domain.model.PageResult;

public interface ListOrdersUseCase {

    /** Pedidos del restaurante del empleado, filtrados por estado. La página empieza en 0. */
    PageResult<Order> listOrders(OrderStatus estado, int page, int size);
}
