package com.caliq.calorie_service.repository;

import com.caliq.calorie_service.models.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyseRepository extends JpaRepository<ReportEntity, Long> {
    Optional <ReportEntity> findByUserIdAndDate(Long userId, LocalDate date);
}
