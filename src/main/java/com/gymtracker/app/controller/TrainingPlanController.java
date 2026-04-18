package com.gymtracker.app.controller;

import com.gymtracker.app.domain.TrainingPlan;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;
import com.gymtracker.app.dto.response.MessageResponse;
import com.gymtracker.app.dto.response.TrainingPlanDTO;
import com.gymtracker.app.mapper.TrainingPlanMapper;
import com.gymtracker.app.service.TrainingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class TrainingPlanController {
    private final TrainingPlanService trainingPlanService;
    private final TrainingPlanMapper trainingPlanMapper;
    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<MessageResponse> createCustomTrainingPlan(
            @Valid @RequestBody TrainingPlanCreationRequest trainingPlanCreationRequest,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        trainingPlanService.generateCustomTrainingPlan(trainingPlanCreationRequest, UUID.fromString(userDetails.getUsername()));

        MessageResponse response = new MessageResponse(
                messageSource.getMessage("message-response.training-plan-created-successfully", null, LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TrainingPlanDTO>> getAllPredefinedTrainingPlans() {
        List<TrainingPlan> trainingPlans = trainingPlanService.getAllPredefinedTrainingPlans();

        List<TrainingPlanDTO> trainingPlanDTOS = trainingPlans.stream()
                .map(trainingPlanMapper::trainingPlanToTrainingPlanDTO)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(trainingPlanDTOS);
    }

    @GetMapping("/user")
    public ResponseEntity<List<TrainingPlanDTO>> getAllUserTrainingPlans(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<TrainingPlan> trainingPlans = trainingPlanService.getUserTrainingPlans(UUID.fromString(userDetails.getUsername()));

        List<TrainingPlanDTO> trainingPlanDTOS = trainingPlans.stream()
                .map(trainingPlanMapper::trainingPlanToTrainingPlanDTO)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(trainingPlanDTOS);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<TrainingPlanDTO> getTrainingPlanById(
            @PathVariable("planId") long planId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        TrainingPlan trainingPlan = trainingPlanService.getTrainingPlanById(planId, UUID.fromString(userDetails.getUsername()));
        TrainingPlanDTO trainingPlanDTO = trainingPlanMapper.trainingPlanToTrainingPlanDTO(trainingPlan);

        return ResponseEntity.status(HttpStatus.OK)
                .body(trainingPlanDTO);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<MessageResponse> deleteTrainingPlanById(
            @PathVariable("planId") long planId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        trainingPlanService.deleteTrainingPlan(planId, UUID.fromString(userDetails.getUsername()));

        MessageResponse response = new MessageResponse(
                messageSource.getMessage("message-response.training-plan-deleted-successfully", null, LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<MessageResponse> updateCustomTrainingPlanById(
            @Valid @RequestBody TrainingPlanCreationRequest trainingPlanCreationRequest,
            @PathVariable("planId") long planId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        trainingPlanService.updateCustomTrainingPlan(
                trainingPlanCreationRequest,
                UUID.fromString(userDetails.getUsername()),
                planId
        );

        MessageResponse response = new MessageResponse(
                messageSource.getMessage("message-response.training-plan-updated-successfully", null, LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
