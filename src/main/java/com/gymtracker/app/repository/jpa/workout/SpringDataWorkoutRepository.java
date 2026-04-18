package com.gymtracker.app.repository.jpa.workout;

import com.gymtracker.app.entity.workout.WorkoutEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpringDataWorkoutRepository extends JpaRepository<WorkoutEntity, Long>, JpaSpecificationExecutor<WorkoutEntity> {
    @Query("""
        SELECT w FROM WorkoutEntity w
        JOIN w.workoutItems item
        WHERE w.user.userId = :userId
        AND item.exercise.exerciseId = :exerciseId
        ORDER BY w.createdAt DESC
    """)
    List<WorkoutEntity> findLastWorkoutsContainingExercise(Long exerciseId, UUID userId, Pageable pageable);

    @Query("""
        SELECT w FROM WorkoutEntity w
        JOIN w.workoutItems item
        WHERE w.user.userId = :userId
        AND item.exercise.exerciseId = :exerciseId
        AND w.createdAt BETWEEN :startDate AND :endDate
        ORDER BY w.createdAt DESC
    """)
    List<WorkoutEntity> findWorkoutsContainingExerciseInPeriod(Long exerciseId, LocalDate startDate, LocalDate endDate, UUID userId);

    List<WorkoutEntity> findWorkoutEntitiesByTraining_IdAndCreatedAtBetweenAndUser_UserId(Long trainingId, LocalDate startDate, LocalDate endDate, UUID userId);
}
