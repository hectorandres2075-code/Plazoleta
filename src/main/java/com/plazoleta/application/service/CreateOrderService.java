package com.plazoleta.application.service;

import com.plazoleta.application.port.input.CreateOrderUseCase;
import com.plazoleta.application.port.output.AuthenticatedUserPort;
import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.exception.ActiveOrderExistsException;
import com.plazoleta.domain.exception.InvalidOrderException;
import com.plazoleta.domain.exception.RestaurantNotFoundException;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderDish;
import com.plazoleta.domain.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderService implements CreateOrderUseCase {

    private final OrderPersistencePort orderPersistencePort;
    private final RestaurantPersistencePort restaurantPersistencePort;
    private final DishPersistencePort dishPersistencePort;
    private final AuthenticatedUserPort authenticatedUserPort;

    public CreateOrderService(OrderPersistencePort orderPersistencePort,
                              RestaurantPersistencePort restaurantPersistencePort,
                              DishPersistencePort dishPersistencePort,
                              AuthenticatedUserPort authenticatedUserPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public Order createOrder(Long idRestaurante, List<OrderDish> platos) {
        if (platos == null || platos.isEmpty()) {
            throw new InvalidOrderException("El pedido debe tener al menos un plato");
        }

        Long idCliente = authenticatedUserPort.currentUserId();
        // El cliente solo puede tener un pedido en proceso a la vez.
        if (orderPersistencePort.existsActiveByClient(idCliente)) {
            throw new ActiveOrderExistsException();
        }

        restaurantPersistencePort.findById(idRestaurante)
                .orElseThrow(() -> new RestaurantNotFoundException(idRestaurante));

        // Si el mismo plato viene repetido, se suman sus cantidades.
        Map<Long, Integer> cantidades = new LinkedHashMap<>();
        for (OrderDish plato : platos) {
            if (plato.getCantidad() == null || plato.getCantidad() <= 0) {
                throw new InvalidOrderException("La cantidad de cada plato debe ser mayor a 0");
            }
            cantidades.merge(plato.getIdPlato(), plato.getCantidad(), Integer::sum);
        }

        List<Dish> encontrados = dishPersistencePort.findAllByIds(cantidades.keySet());
        if (encontrados.size() != cantidades.size()) {
            throw new InvalidOrderException("Alguno de los platos del pedido no existe");
        }
        for (Dish dish : encontrados) {
            if (!dish.getIdRestaurante().equals(idRestaurante)) {
                throw new InvalidOrderException("Todos los platos deben ser del restaurante del pedido");
            }
            if (!dish.isActivo()) {
                throw new InvalidOrderException("El plato '" + dish.getNombre() + "' no está disponible");
            }
        }

        List<OrderDish> items = cantidades.entrySet().stream()
                .map(e -> new OrderDish(e.getKey(), e.getValue()))
                .toList();
        Order order = new Order(null, idCliente, LocalDateTime.now(), OrderStatus.PENDIENTE,
                null, idRestaurante, null, items);
        return orderPersistencePort.save(order);
    }
}
