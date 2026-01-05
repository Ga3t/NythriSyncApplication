package com.caliq.FoodSecretApiConnection.repository;


import com.caliq.FoodSecretApiConnection.models.FoodModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodSecretRepository extends JpaRepository<FoodModel, Long> {

    Optional<FoodModel> findByFoodSecretId(String food_secret_id);

}
