package com.plazoleta.infrastructure.input.rest;

import com.plazoleta.application.port.input.AssignOrderUseCase;
import com.plazoleta.application.port.input.CreateOrderUseCase;
import com.plazoleta.application.port.input.ListOrdersUseCase;
import com.plazoleta.domain.model.Order;
import com.plazoleta.domain.model.OrderDish;
import com.plazoleta.domain.model.OrderStatus;
import com.plazoleta.domain.model.PageResult;
import com.plazoleta.infrastructure.input.rest.dto.OrderRequest;
import com.plazoleta.infrastructure.input.rest.dto.PageResponse;
import com.plazoleta.infrastructure.input.rest.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Pedidos")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final AssignOrderUseCase assignOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase, ListOrdersUseCase listOrdersUseCase,
                           AssignOrderUseCase assignOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.assignOrderUseCase = assignOrderUseCase;
    }

    @Operation(summary = "Asignarse a un pedido",
            description = "Solo el EMPLEADO del restaurante. El pedido debe estar PENDIENTE; pasa a EN_PREPARACION "
                    + "y queda con el empleado como chef asignado")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/assign")
    public OrderResponse assign(@PathVariable Long id) {
        return OrderResponse.from(assignOrderUseCase.assignOrder(id));
    }

    @Operation(summary = "Listar pedidos por estado",
            description = "Solo el EMPLEADO. Devuelve únicamente los pedidos de su restaurante, paginados "
                    + "(page empieza en 0). El PIN nunca se muestra")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public PageResponse<OrderResponse> list(@RequestParam OrderStatus estado,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        PageResult<Order> result = listOrdersUseCase.listOrders(estado, page, size);
        return new PageResponse<>(
                result.getContent().stream().map(OrderResponse::from).toList(),
                result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages());
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
