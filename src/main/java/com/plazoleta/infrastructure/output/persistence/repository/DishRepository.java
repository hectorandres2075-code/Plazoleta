package com.plazoleta.infrastructure.output.persistence.repository;

import com.plazoleta.infrastructure.output.persistence.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<DishEntity, Long> {

    Page<DishEntity> findByIdRestauranteAndActivoTrue(Long idRestaurante, Pageable pageable);

    Page<DishEntity> findByIdRestauranteAndActivoTrueAndCategoriaIgnoreCase(Long idRestaurante,
                                                                            String categoria,
                                                                            Pageable pageable);
}
