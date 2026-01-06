package com.caliq.calorie_service.repository;
import com.caliq.calorie_service.models.entity.MealEntity;
import com.caliq.calorie_service.models.entity.UserModel;
import com.caliq.calorie_service.models.types.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface CaloryRepository extends JpaRepository<MealEntity,Long> {
    List<MealEntity> findByUser_IdAndDateTime(
            Long userId,
            LocalDate date);
    List<MealEntity> findByUserId(Long userId);
    Long user(UserModel user);
    Optional<MealEntity> findByUser_IdAndDateTimeAndMealType(Long userId,
                                                         LocalDate dateTime,
                                                         MealType mealType);
}