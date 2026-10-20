package com.plazoleta.infrastructure.input.rest;

import com.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.plazoleta.domain.model.Restaurant;
import com.plazoleta.infrastructure.input.rest.dto.RestaurantRequest;
import com.plazoleta.infrastructure.input.rest.dto.RestaurantResponse;
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
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurantes")
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase) {
        this.createRestaurantUseCase = createRestaurantUseCase;
    }

    @Operation(summary = "Crear restaurante", description = "Solo el ADMINISTRADOR. El propietario debe existir y tener rol PROPIETARIO")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody RestaurantRequest request) {
        Restaurant restaurant = new Restaurant(null, request.getNombre(), request.getNit(),
                request.getDireccion(), request.getTelefono(), request.getUrlLogo(), request.getIdPropietario());

        Restaurant saved = createRestaurantUseCase.createRestaurant(restaurant);
        RestaurantResponse body = new RestaurantResponse(saved.getId(), saved.getNombre(), saved.getNit(),
                saved.getDireccion(), saved.getTelefono(), saved.getUrlLogo(), saved.getIdPropietario());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
