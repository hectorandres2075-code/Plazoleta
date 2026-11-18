package com.plazoleta.infrastructure.input.rest;

import com.plazoleta.application.port.input.CreateDishUseCase;
import com.plazoleta.application.port.input.UpdateDishStatusUseCase;
import com.plazoleta.application.port.input.UpdateDishUseCase;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.infrastructure.input.rest.dto.DishRequest;
import com.plazoleta.infrastructure.input.rest.dto.DishStatusRequest;
import com.plazoleta.infrastructure.input.rest.dto.DishUpdateRequest;
import com.plazoleta.infrastructure.input.rest.dto.DishResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dishes")
@Tag(name = "Platos")
public class DishController {

    private final CreateDishUseCase createDishUseCase;
    private final UpdateDishUseCase updateDishUseCase;
    private final UpdateDishStatusUseCase updateDishStatusUseCase;

    public DishController(CreateDishUseCase createDishUseCase, UpdateDishUseCase updateDishUseCase,
                          UpdateDishStatusUseCase updateDishStatusUseCase) {
        this.createDishUseCase = createDishUseCase;
        this.updateDishUseCase = updateDishUseCase;
        this.updateDishStatusUseCase = updateDishStatusUseCase;
    }

    @Operation(summary = "Habilitar o deshabilitar plato", description = "Solo el PROPIETARIO del restaurante. activo=false oculta el plato del menú")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/status")
    public DishResponse updateStatus(@PathVariable Long id, @Valid @RequestBody DishStatusRequest request) {
        Dish s = updateDishStatusUseCase.updateStatus(id, request.getActivo());
        return new DishResponse(s.getId(), s.getNombre(), s.getPrecio(), s.getDescripcion(),
                s.getUrlImagen(), s.getCategoria(), s.isActivo(), s.getIdRestaurante());
    }

    @Operation(summary = "Modificar plato", description = "Solo el PROPIETARIO del restaurante. Cambia únicamente precio y descripción")
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}")
    public DishResponse update(@PathVariable Long id, @Valid @RequestBody DishUpdateRequest request) {
        Dish s = updateDishUseCase.updateDish(id, request.getPrecio(), request.getDescripcion());
        return new DishResponse(s.getId(), s.getNombre(), s.getPrecio(), s.getDescripcion(),
                s.getUrlImagen(), s.getCategoria(), s.isActivo(), s.getIdRestaurante());
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
