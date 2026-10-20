package com.plazoleta.infrastructure.output.persistence.adapter;

import com.plazoleta.application.port.output.DishPersistencePort;
import com.plazoleta.domain.model.Dish;
import com.plazoleta.infrastructure.output.persistence.entity.DishEntity;
import com.plazoleta.infrastructure.output.persistence.repository.DishRepository;

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
