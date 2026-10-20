package com.plazoleta.infrastructure.input.rest;

import com.plazoleta.application.port.input.CreateDishUseCase;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.infrastructure.input.rest.dto.DishRequest;
import com.plazoleta.infrastructure.input.rest.dto.DishResponse;
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
@RequestMapping("/api/v1/dishes")
@Tag(name = "Platos")
public class DishController {

    private final CreateDishUseCase createDishUseCase;

    public DishController(CreateDishUseCase createDishUseCase) {
        this.createDishUseCase = createDishUseCase;
    }

    @Operation(summary = "Crear plato", description = "Solo el PROPIETARIO, y únicamente en su propio restaurante")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<DishResponse> create(@Valid @RequestBody DishRequest request) {
        Dish dish = new Dish(null, request.getNombre(), request.getPrecio(), request.getDescripcion(),
                request.getUrlImagen(), request.getCategoria(), true, request.getIdRestaurante());

        Dish s = createDishUseCase.createDish(dish);
        DishResponse body = new DishResponse(s.getId(), s.getNombre(), s.getPrecio(), s.getDescripcion(),
                s.getUrlImagen(), s.getCategoria(), s.isActivo(), s.getIdRestaurante());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
