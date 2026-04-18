package com.gymtracker.app.repository.jpa.workout;

import com.gymtracker.app.entity.workout.WorkoutEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorkoutSpecifications {
    private WorkoutSpecifications() {}

    public static Specification<WorkoutEntity> filterWorkouts(Long trainingPlanId, LocalDate startDate, LocalDate endDate, UUID userId) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (trainingPlanId != null) {
                predicates.add(criteriaBuilder.equal(root.get("training").get("id"), trainingPlanId));
            }

            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            if (userId == null) {
                throw new IllegalArgumentException("User ID must not be null");
            } else {
                predicates.add(criteriaBuilder.equal(root.get("user").get("userId"), userId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
