package com.caliq.api_conection_service.repository;

import com.caliq.api_conection_service.model.FoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface OpenFoodFactRepository extends JpaRepository<FoodEntity, Long> {

    Optional<FoodEntity> findById(Long id);
}
