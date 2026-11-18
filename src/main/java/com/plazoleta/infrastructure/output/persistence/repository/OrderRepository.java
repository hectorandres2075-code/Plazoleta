package com.plazoleta.infrastructure.output.persistence.repository;

import com.plazoleta.domain.model.OrderStatus;
import com.plazoleta.infrastructure.output.persistence.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    boolean existsByIdClienteAndEstadoIn(Long idCliente, Collection<OrderStatus> estados);

    Page<OrderEntity> findByIdRestauranteAndEstado(Long idRestaurante, OrderStatus estado, Pageable pageable);
}
