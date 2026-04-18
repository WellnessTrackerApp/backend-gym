package com.gymtracker.app.repository.jpa.training;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.entity.TrainingPlanEntity;
import com.gymtracker.app.mapper.TrainingPlanMapper;
import com.gymtracker.app.repository.TrainingPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TrainingPlanRepositoryAdapter implements TrainingPlanRepository {
    private final SpringDataJpaTrainingPlanRepository repository;
    private final TrainingPlanMapper trainingPlanMapper;

    @Override
    public TrainingPlan save(TrainingPlan trainingPlan) {
        TrainingPlanEntity trainingPlanEntity = trainingPlanMapper.trainingPlanToTrainingPlanEntity(trainingPlan);
        TrainingPlanEntity savedTrainingPlanEntity = repository.save(trainingPlanEntity);
        return trainingPlanMapper.trainingPlanEntityToTrainingPlan(savedTrainingPlanEntity);
    }

    @Override
    public List<TrainingPlan> findAllPredefinedPlans() {
        return repository.findAllByIsCustomFalseAndIsDeletedFalse().stream()
                .map(trainingPlanMapper::trainingPlanEntityToTrainingPlan)
                .toList();
    }

    @Override
    public boolean existsInUserAccessiblePlans(Long trainingId, UUID userId) {
        TrainingPlanEntity trainingPlanEntity = repository.findById(trainingId)
                .orElse(null);

        return trainingPlanEntity != null
                &&
                (!trainingPlanEntity.isCustom()
                        ||
                 trainingPlanEntity.getOwner().getUserId().equals(userId));
    }

    @Override
    public boolean isDeleted(long trainingPlanId) {
        TrainingPlanEntity trainingPlanEntity = repository.findById(trainingPlanId)
                .orElse(null);
        return trainingPlanEntity == null || trainingPlanEntity.isDeleted();
    }

    @Override
    public void deleteById(long trainingPlanId) {
        TrainingPlanEntity trainingPlanEntity = repository.findById(trainingPlanId).orElse(null);
        if (trainingPlanEntity == null)
            return;

        trainingPlanEntity.setDeleted(true);
        repository.save(trainingPlanEntity);
    }

    @Override
    public Optional<TrainingPlan> findById(long trainingPlanId) {
        return repository.findById(trainingPlanId)
                .map(trainingPlanMapper::trainingPlanEntityToTrainingPlan);
    }
}
