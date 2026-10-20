package com.plazoleta.infrastructure.input.rest;

import com.plazoleta.application.port.input.ListDishesUseCase;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.domain.model.PageResult;
import com.plazoleta.infrastructure.input.rest.dto.DishSummaryResponse;
import com.plazoleta.infrastructure.input.rest.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/dishes")
@Tag(name = "Menú del restaurante")
public class RestaurantDishController {

    private final ListDishesUseCase listDishesUseCase;

    public RestaurantDishController(ListDishesUseCase listDishesUseCase) {
        this.listDishesUseCase = listDishesUseCase;
    }

    @Operation(summary = "Listar platos de un restaurante",
            description = "Solo el CLIENTE. Solo platos activos, agrupados por categoría, paginados. "
                    + "Se puede filtrar por una categoría")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public PageResponse<DishSummaryResponse> list(@PathVariable Long restaurantId,
                                                  @RequestParam(required = false) String categoria,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        PageResult<Dish> result = listDishesUseCase.listDishes(restaurantId, categoria, page, size);
        return new PageResponse<>(
                result.getContent().stream()
                        .map(d -> new DishSummaryResponse(d.getId(), d.getNombre(), d.getPrecio(),
                                d.getDescripcion(), d.getUrlImagen(), d.getCategoria()))
                        .toList(),
                result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }
}
