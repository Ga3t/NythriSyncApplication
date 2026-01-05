package com.caliq.calorie_service.repository;


import com.caliq.calorie_service.models.entity.UserModel;
import com.caliq.calorie_service.models.entity.WeightLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeightLogsRepository extends JpaRepository<WeightLogsEntity, Long> {
    Optional<WeightLogsEntity> findByUserAndDate(UserModel user, LocalDate date);

    List<WeightLogsEntity> findAllByUser_IdAndDateBetweenOrderByDateAsc(Long userId,
                                                                        LocalDate start,
                                                                        LocalDate end);
}
