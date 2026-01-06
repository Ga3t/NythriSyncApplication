package com.caliq.calorie_service.repository;
import com.caliq.calorie_service.models.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
@Repository
public interface UserRepository extends JpaRepository<UserModel,Long> {
    @Query("select u.waterNeeds from UserModel u where u.id = :userId")
    BigDecimal findWaterNeedsByUserId(@Param("userId") Long userId);
    @Query("select u.bmr from UserModel u where u.id = :userId")
    BigDecimal findBmrByUserId(@Param("userId") Long userId);
}