package com.plazoleta.infrastructure.output.persistence.repository;

import com.plazoleta.infrastructure.output.persistence.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

    boolean existsByNit(String nit);
}
