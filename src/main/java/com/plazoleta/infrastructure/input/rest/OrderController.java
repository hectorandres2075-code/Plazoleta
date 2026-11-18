package com.plazoleta.infrastructure.input.rest;

import com.plazoleta.application.port.input.CreateOrderUseCase;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderDish;
import com.plazoleta.infrastructure.input.rest.dto.OrderRequest;
import com.plazoleta.infrastructure.input.rest.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Pedidos")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @Operation(summary = "Realizar pedido",
            description = "Solo el CLIENTE. Los platos deben ser activos y del mismo restaurante. "
                    + "Solo se permite un pedido en proceso a la vez. Queda en estado PENDIENTE")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        Order created = createOrderUseCase.createOrder(
                request.getIdRestaurante(),
                request.getPlatos().stream()
                        .map(p -> new OrderDish(p.getIdPlato(), p.getCantidad()))
                        .toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(created));
    }
}
