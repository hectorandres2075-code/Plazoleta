package com.plazoleta.infrastructure.output.persistence.adapter;

import com.plazoleta.application.port.output.OrderPersistencePort;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderDish;
import com.plazoleta.domain.model.OrderStatus;
import com.plazoleta.domain.model.PageResult;
import com.plazoleta.infrastructure.output.persistence.entity.OrderDishEntity;
import com.plazoleta.infrastructure.output.persistence.entity.OrderEntity;
import com.plazoleta.infrastructure.output.persistence.repository.OrderRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public class OrderPersistenceAdapter implements OrderPersistencePort {

    private static final List<OrderStatus> ESTADOS_ACTIVOS =
            List.of(OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO);

    private final OrderRepository orderRepository;

    public OrderPersistenceAdapter(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity;
        if (order.getId() == null) {
            entity = new OrderEntity();
            entity.setIdCliente(order.getIdCliente());
            entity.setFecha(order.getFecha());
            entity.setIdRestaurante(order.getIdRestaurante());
            for (OrderDish plato : order.getPlatos()) {
                OrderDishEntity linea = new OrderDishEntity();
                linea.setPedido(entity);
                linea.setIdPlato(plato.getIdPlato());
                linea.setCantidad(plato.getCantidad());
                entity.getPlatos().add(linea);
            }
        } else {
            // Un pedido existente solo cambia de estado, chef y pin; sus platos no se tocan.
            entity = orderRepository.findById(order.getId())
                    .orElseThrow(() -> new IllegalStateException("El pedido " + order.getId() + " no existe"));
        }
        entity.setEstado(order.getEstado());
        entity.setIdChef(order.getIdChef());
        entity.setPin(order.getPin());

        return toModel(orderRepository.save(entity));
    }

    @Override
    public boolean existsActiveByClient(Long idCliente) {
        return orderRepository.existsByIdClienteAndEstadoIn(idCliente, ESTADOS_ACTIVOS);
    }

    @Override
    public PageResult<Order> findByRestaurantAndStatus(Long idRestaurante, OrderStatus estado, int page, int size) {
        // Del más antiguo al más reciente: es el orden en que el restaurante debe atenderlos.
        Page<OrderEntity> result = orderRepository.findByIdRestauranteAndEstado(
                idRestaurante, estado, PageRequest.of(page, size, Sort.by("fecha")));
        return new PageResult<>(
                result.getContent().stream().map(this::toModel).toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    private Order toModel(OrderEntity e) {
        List<OrderDish> platos = e.getPlatos().stream()
                .map(l -> new OrderDish(l.getIdPlato(), l.getCantidad()))
                .toList();
        return new Order(e.getId(), e.getIdCliente(), e.getFecha(), e.getEstado(), e.getIdChef(),
                e.getIdRestaurante(), e.getPin(), platos);
    }
}
