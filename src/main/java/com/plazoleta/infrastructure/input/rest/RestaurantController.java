package com.plazoleta.infrastructure.input.rest;

import com.plazoleta.application.port.input.CreateRestaurantUseCase;
import com.plazoleta.application.port.input.GetRestaurantUseCase;
import com.plazoleta.application.port.input.ListRestaurantsUseCase;
import com.plazoleta.domain.model.PageResult;
import com.plazoleta.domain.model.Restaurant;
import com.plazoleta.infrastructure.input.rest.dto.OwnerIdResponse;
import com.plazoleta.infrastructure.input.rest.dto.PageResponse;
import com.plazoleta.infrastructure.input.rest.dto.RestaurantRequest;
import com.plazoleta.infrastructure.input.rest.dto.RestaurantResponse;
import com.plazoleta.infrastructure.input.rest.dto.RestaurantSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurantes")
public class RestaurantController {

    private final CreateRestaurantUseCase createRestaurantUseCase;
    private final GetRestaurantUseCase getRestaurantUseCase;
    private final ListRestaurantsUseCase listRestaurantsUseCase;

    public RestaurantController(CreateRestaurantUseCase createRestaurantUseCase,
                                GetRestaurantUseCase getRestaurantUseCase,
                                ListRestaurantsUseCase listRestaurantsUseCase) {
        this.createRestaurantUseCase = createRestaurantUseCase;
        this.getRestaurantUseCase = getRestaurantUseCase;
        this.listRestaurantsUseCase = listRestaurantsUseCase;
    }

    @Operation(summary = "Listar restaurantes", description = "Solo el CLIENTE. Orden alfabético por nombre, paginado (page empieza en 0)")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public PageResponse<RestaurantSummaryResponse> list(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        PageResult<Restaurant> result = listRestaurantsUseCase.listRestaurants(page, size);
        return new PageResponse<>(
                result.getContent().stream()
                        .map(r -> new RestaurantSummaryResponse(r.getNombre(), r.getUrlLogo()))
                        .toList(),
                result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Operation(summary = "Consultar el propietario de un restaurante", description = "Lo usa el microservicio de Usuarios")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}/owner")
    public OwnerIdResponse getOwner(@PathVariable Long id) {
        return new OwnerIdResponse(getRestaurantUseCase.getRestaurant(id).getIdPropietario());
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
