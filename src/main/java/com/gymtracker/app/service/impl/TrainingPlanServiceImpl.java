package com.gymtracker.app.service.impl;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.domain.User;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;
import com.gymtracker.app.exception.ExerciseDoesNotExistException;
import com.gymtracker.app.exception.TrainingDoesNotExistException;
import com.gymtracker.app.exception.UserDoesNotExistException;
import com.gymtracker.app.repository.ExerciseRepository;
import com.gymtracker.app.repository.TrainingPlanRepository;
import com.gymtracker.app.repository.UserRepository;
import com.gymtracker.app.service.TrainingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrainingPlanServiceImpl implements TrainingPlanService {
    private final UserRepository userRepository;
    private final TrainingPlanRepository trainingPlanRepository;
    private final ExerciseRepository exerciseRepository;

    @Override
    @Transactional
    public void generateCustomTrainingPlan(TrainingPlanCreationRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("creating-plan"));

        List<TrainingPlan.PlanItem> trainingPlanItems = request.planItems()
                .stream()
                .map(planItem -> {
                    Exercise exercise = exerciseRepository.findExerciseAccessibleByUser(planItem.exerciseId(), userId)
                        .orElseThrow(() -> new ExerciseDoesNotExistException("id-not-found", planItem.exerciseId()));

                    return new TrainingPlan.PlanItem(exercise, planItem.defaultSets());
                })
                .toList();

        TrainingPlan trainingPlan = user.createCustomTrainingPlan(request.planName(), trainingPlanItems);
        trainingPlanRepository.save(trainingPlan);
    }

    @Override
    public List<TrainingPlan> getAllPredefinedTrainingPlans() {
        return trainingPlanRepository.findAllPredefinedPlans();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingPlan> getUserTrainingPlans(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserDoesNotExistException("retrieving-plans"));

        return owner.getPlans();
    }

    @Override
    public TrainingPlan getTrainingPlanById(long trainingPlanId, UUID userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("retrieving-plan"));
        List<TrainingPlan> userPlans = owner.getPlans();

        Optional<TrainingPlan> trainingPlan = userPlans.stream()
                .filter(plan -> plan.getId().equals(trainingPlanId))
                .findFirst();

        return trainingPlan.orElseGet(() -> getAllPredefinedTrainingPlans().stream()
                .filter(plan -> plan.getId().equals(trainingPlanId))
                .findFirst()
                .orElseThrow(() -> new TrainingDoesNotExistException("not-found")));
    }

    @Override
    public TrainingPlan getTrainingPlanByIdForWorkoutHistory(long trainingPlanId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserDoesNotExistException("retrieving-plan");
        }

        if (!trainingPlanRepository.existsInUserAccessiblePlans(trainingPlanId, userId)) {
            throw new TrainingDoesNotExistException("not-accessible");
        }

        return trainingPlanRepository.findById(trainingPlanId)
                .orElseThrow(() -> new TrainingDoesNotExistException("not-found"));
    }

    @Override
    @Transactional
    public void deleteTrainingPlan(long planId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("deleting-plan"));

        user.removeCustomTrainingPlan(planId);

        trainingPlanRepository.deleteById(planId);
    }

    @Override
    @Transactional
    public void updateCustomTrainingPlan(TrainingPlanCreationRequest request, UUID userId, long planId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("updating-plan"));

        List<TrainingPlan.PlanItem> trainingPlanItems = request.planItems()
                .stream()
                .map(planItem -> {
                    Exercise exercise = exerciseRepository.findExerciseAccessibleByUser(planItem.exerciseId(), userId)
                            .orElseThrow(() -> new ExerciseDoesNotExistException("id-not-found", planItem.exerciseId()));

                    return new TrainingPlan.PlanItem(exercise, planItem.defaultSets());
                })
                .toList();

        TrainingPlan trainingPlan = user.updateCustomTrainingPlan(planId, request.planName(), trainingPlanItems);
        trainingPlanRepository.save(trainingPlan);
    }
}
