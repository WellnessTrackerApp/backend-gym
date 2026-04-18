package com.gymtracker.app.controller;

import com.gymtracker.app.domain.Exercise;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.dto.response.ExerciseDTO;
import com.gymtracker.app.dto.response.MessageResponse;
import com.gymtracker.app.mapper.ExerciseMapper;
import com.gymtracker.app.service.ExerciseService;
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

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;
    private final ExerciseMapper exerciseMapper;
    private final MessageSource messageSource;

    @PostMapping
    public ResponseEntity<ExerciseDTO> createCustomExercise(
            @Valid @RequestBody ExerciseCreationRequest exerciseCreationRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        Exercise exercise = exerciseMapper.exerciseCreationRequestToExercise(exerciseCreationRequest);
        Exercise createdExercise = exerciseService.createCustomExercise(exercise, UUID.fromString(userDetails.getUsername()));
        ExerciseDTO exerciseDTO = exerciseMapper.exerciseToExerciseDTO(createdExercise);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exerciseDTO);
    }

    @GetMapping("user")
    public ResponseEntity<Set<ExerciseDTO>> getUserExercises(@AuthenticationPrincipal UserDetails userDetails) {
        Set<Exercise> userExercises = exerciseService.getUserExercises(UUID.fromString(userDetails.getUsername()));

        Set<ExerciseDTO> exerciseDTOS = userExercises.stream()
                .map(exerciseMapper::exerciseToExerciseDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(exerciseDTOS);
    }

    @GetMapping
    public ResponseEntity<Set<ExerciseDTO>> getPredefinedExercises() {
        Set<Exercise> predefinedExercises = exerciseService.getPredefinedExercises();

        Set<ExerciseDTO> exerciseDTOS = predefinedExercises.stream()
                .map(exerciseMapper::exerciseToExerciseDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(exerciseDTOS);
    }

    @DeleteMapping("/{exerciseId}")
    public ResponseEntity<MessageResponse> deleteCustomExercise(
            @PathVariable long exerciseId,
            @AuthenticationPrincipal UserDetails userDetails) {
        exerciseService.deleteCustomExercise(exerciseId, UUID.fromString(userDetails.getUsername()));

        MessageResponse messageResponse = new MessageResponse(
                messageSource.getMessage("message-response.exercise-deleted-successfully", null, LocaleContextHolder.getLocale())
        );
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PutMapping("/{exerciseId}")
    public ResponseEntity<ExerciseDTO> updateCustomExercise(
            @PathVariable long exerciseId,
            @Valid @RequestBody ExerciseCreationRequest exerciseCreationRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        Exercise updatedExercise = exerciseService.updateCustomExercise(
                exerciseId,
                exerciseCreationRequest,
                UUID.fromString(userDetails.getUsername())
        );

        ExerciseDTO exerciseDTO = exerciseMapper.exerciseToExerciseDTO(updatedExercise);

        return ResponseEntity.ok(exerciseDTO);
    }
}
