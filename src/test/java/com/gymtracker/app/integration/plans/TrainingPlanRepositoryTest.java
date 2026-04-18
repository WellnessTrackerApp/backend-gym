package com.gymtracker.app.integration.plans;

import com.gymtracker.app.domain.ExerciseCategory;
import com.gymtracker.app.entity.ExerciseEntity;
import com.gymtracker.app.entity.PlanItemEntity;
import com.gymtracker.app.entity.TrainingPlanEntity;
import com.gymtracker.app.integration.BaseIntegrationTest;
import com.gymtracker.app.repository.jpa.exercise.SpringDataJpaExerciseRepository;
import com.gymtracker.app.repository.jpa.training.SpringDataJpaTrainingPlanRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class TrainingPlanRepositoryTest extends BaseIntegrationTest {
    @Autowired
    private SpringDataJpaTrainingPlanRepository trainingPlanRepository;

    @Autowired
    private SpringDataJpaExerciseRepository exerciseRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(trainingPlanRepository);
    }

    @Test
    void givenValidData_whenSaveMethodCalled_shouldSaveTrainingPlan() {
        TrainingPlanEntity trainingPlanEntity = TrainingPlanEntity.builder()
                .name("My Training Plan")
                .isCustom(false)
                .planItems(List.of(PlanItemEntity.builder().exercise(createTestExerciseEntity()).build()))
                .build();

        TrainingPlanEntity savedTrainingPlanEntity = trainingPlanRepository.save(trainingPlanEntity);

        Assertions.assertNotNull(savedTrainingPlanEntity);
        Assertions.assertEquals(trainingPlanEntity.getName(), savedTrainingPlanEntity.getName());
        Assertions.assertNotNull(savedTrainingPlanEntity.getId());
    }

    @Test
    void givenValidData_whenGetMethodCalled_shouldGetAllTrainingPlans() {
        trainingPlanRepository.deleteAll();

        ExerciseEntity exerciseEntity = createTestExerciseEntity();
        exerciseRepository.save(exerciseEntity);

        TrainingPlanEntity trainingPlanEntity = TrainingPlanEntity.builder()
                .name("My Training Plan")
                .isCustom(false)
                .planItems(List.of(PlanItemEntity.builder().exercise(exerciseEntity).defaultSets(3).build()))
                .build();

        trainingPlanRepository.save(trainingPlanEntity);

        Iterable<TrainingPlanEntity> trainingPlanEntities = trainingPlanRepository.findAll();
        Assertions.assertTrue(trainingPlanEntities.iterator().hasNext());

        TrainingPlanEntity retrievedPlan = trainingPlanEntities.iterator().next();
        Assertions.assertNotNull(retrievedPlan);
        Assertions.assertEquals("My Training Plan", retrievedPlan.getName());
    }

    @Test
    void givenPredefinedTrainingPlan_whenGetAllPredefinedPlansCalled_shouldReturnThePredefinedPlan() {
        ExerciseEntity exerciseEntity = createTestExerciseEntity();
        exerciseRepository.save(exerciseEntity);

        TrainingPlanEntity trainingPlanEntity = TrainingPlanEntity.builder()
                .name("Predefined Training Plan")
                .isCustom(false)
                .planItems(List.of(PlanItemEntity.builder().exercise(exerciseEntity).defaultSets(3).build()))
                .build();
        trainingPlanRepository.save(trainingPlanEntity);

        List<TrainingPlanEntity> predefinedPlans = trainingPlanRepository.findAllByIsCustomFalseAndIsDeletedFalse();

        Assertions.assertFalse(predefinedPlans.isEmpty());
        Assertions.assertTrue(predefinedPlans.stream()
                .anyMatch(tpe -> tpe.getName().equals(trainingPlanEntity.getName()))
        );
    }

    @Test
    void givenCustomTrainingPlan_whenGetAllPredefinedPlansCalled_shouldNotReturnTheCustomPlan() {
        trainingPlanRepository.deleteAll();

        ExerciseEntity exerciseEntity = createTestExerciseEntity();
        exerciseRepository.save(exerciseEntity);

        TrainingPlanEntity trainingPlanEntity = TrainingPlanEntity.builder()
                .name("Custom Training Plan")
                .isCustom(true)
                .planItems(List.of(PlanItemEntity.builder().exercise(exerciseEntity).defaultSets(3).build()))
                .build();
        trainingPlanRepository.save(trainingPlanEntity);

        List<TrainingPlanEntity> predefinedPlans = trainingPlanRepository.findAllByIsCustomFalseAndIsDeletedFalse();
        Assertions.assertTrue(predefinedPlans.isEmpty());
    }

    private ExerciseEntity createTestExerciseEntity() {
        return ExerciseEntity.builder()
                .name("Test Exercise")
                .isCustom(true)
                .category(ExerciseCategory.UNCATEGORIZED)
                .build();
    }

}
