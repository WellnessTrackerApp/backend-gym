package com.gymtracker.app.repository.jpa.training;

import com.gymtracker.app.entity.TrainingPlanEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataJpaTrainingPlanRepository extends CrudRepository<TrainingPlanEntity, Long> {
    List<TrainingPlanEntity> findAllByIsCustomFalseAndIsDeletedFalse();
    boolean existsByIdAndOwnerUserId(Long id, UUID ownerUserId);
    boolean existsByIdAndIsCustomIsFalse(Long id);
}
