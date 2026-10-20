package com.plazoleta.infrastructure.output.persistence.adapter;

import com.plazoleta.application.port.output.RestaurantPersistencePort;
import com.plazoleta.domain.model.Restaurant;
import com.plazoleta.infrastructure.output.persistence.entity.RestaurantEntity;
import com.plazoleta.infrastructure.output.persistence.repository.RestaurantRepository;

import com.plazoleta.domain.model.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class RestaurantPersistenceAdapter implements RestaurantPersistencePort {

    private final RestaurantRepository restaurantRepository;

    public RestaurantPersistenceAdapter(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public boolean existsByNit(String nit) {
        return restaurantRepository.existsByNit(nit);
    }

    @Override
    public Optional<Restaurant> findById(Long id) {
        return restaurantRepository.findById(id)
                .map(e -> new Restaurant(e.getId(), e.getNombre(), e.getNit(), e.getDireccion(),
                        e.getTelefono(), e.getUrlLogo(), e.getIdPropietario()));
    }

    @Override
    public PageResult<Restaurant> findAllOrderedByName(int page, int size) {
        Page<RestaurantEntity> result = restaurantRepository.findAll(PageRequest.of(page, size, Sort.by("nombre")));
        return new PageResult<>(
                result.getContent().stream()
                        .map(e -> new Restaurant(e.getId(), e.getNombre(), e.getNit(), e.getDireccion(),
                                e.getTelefono(), e.getUrlLogo(), e.getIdPropietario()))
                        .toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantEntity e = new RestaurantEntity();
        e.setId(restaurant.getId());
        e.setNombre(restaurant.getNombre());
        e.setNit(restaurant.getNit());
        e.setDireccion(restaurant.getDireccion());
        e.setTelefono(restaurant.getTelefono());
        e.setUrlLogo(restaurant.getUrlLogo());
        e.setIdPropietario(restaurant.getIdPropietario());

        RestaurantEntity saved = restaurantRepository.save(e);
        return new Restaurant(saved.getId(), saved.getNombre(), saved.getNit(), saved.getDireccion(),
                saved.getTelefono(), saved.getUrlLogo(), saved.getIdPropietario());
    }
}
