package com.caliq.calorie_service.repository;
import com.caliq.calorie_service.models.entity.CaloryLogsEntity;
import com.caliq.calorie_service.models.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface CaloryLogsRepository extends JpaRepository<CaloryLogsEntity, Long> {
    Optional<CaloryLogsEntity> findByUserAndDate(UserModel user, LocalDate date);
    List<CaloryLogsEntity> findAllByUser_IdAndDateBetweenOrderByDateAsc(
            Long userId, LocalDate start, LocalDate end);
}