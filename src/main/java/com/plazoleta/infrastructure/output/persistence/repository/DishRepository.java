package com.plazoleta.infrastructure.output.persistence.repository;

import com.plazoleta.infrastructure.output.persistence.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<DishEntity, Long> {
}
