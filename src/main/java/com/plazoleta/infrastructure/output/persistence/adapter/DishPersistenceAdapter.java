package com.plazoleta.infrastructure.output.persistence.adapter;

import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.infrastructure.output.persistence.entity.DishEntity;
import com.plazoleta.infrastructure.output.persistence.repository.DishRepository;

import com.plazoleta.domain.model.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DishPersistenceAdapter implements DishPersistencePort {

    private final DishRepository dishRepository;

    public DishPersistenceAdapter(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id)
                .map(s -> new Dish(s.getId(), s.getNombre(), s.getPrecio(), s.getDescripcion(), s.getUrlImagen(),
                        s.getCategoria(), s.isActivo(), s.getIdRestaurante()));
    }

    @Override
    public List<Dish> findAllByIds(Collection<Long> ids) {
        return dishRepository.findAllById(ids).stream()
                .map(s -> new Dish(s.getId(), s.getNombre(), s.getPrecio(), s.getDescripcion(), s.getUrlImagen(),
                        s.getCategoria(), s.isActivo(), s.getIdRestaurante()))
                .toList();
    }

    @Override
    public PageResult<Dish> findActiveByRestaurant(Long restaurantId, String categoria, int page, int size) {
        // Agrupados por categoría: primero se ordena por categoría y luego por nombre.
        Pageable pageable = PageRequest.of(page, size, Sort.by("categoria", "nombre"));
        Page<DishEntity> result = (categoria == null)
                ? dishRepository.findByIdRestauranteAndActivoTrue(restaurantId, pageable)
                : dishRepository.findByIdRestauranteAndActivoTrueAndCategoriaIgnoreCase(restaurantId, categoria, pageable);

        return new PageResult<>(
                result.getContent().stream()
                        .map(s -> new Dish(s.getId(), s.getNombre(), s.getPrecio(), s.getDescripcion(),
                                s.getUrlImagen(), s.getCategoria(), s.isActivo(), s.getIdRestaurante()))
                        .toList(),
                result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Override
    public Dish save(Dish dish) {
        DishEntity e = new DishEntity();
        e.setId(dish.getId());
        e.setNombre(dish.getNombre());
        e.setPrecio(dish.getPrecio());
        e.setDescripcion(dish.getDescripcion());
        e.setUrlImagen(dish.getUrlImagen());
        e.setCategoria(dish.getCategoria());
        e.setActivo(dish.isActivo());
        e.setIdRestaurante(dish.getIdRestaurante());

        DishEntity s = dishRepository.save(e);
        return new Dish(s.getId(), s.getNombre(), s.getPrecio(), s.getDescripcion(), s.getUrlImagen(),
                s.getCategoria(), s.isActivo(), s.getIdRestaurante());
    }
}
