package com.gymtracker.app.integration.workout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymtracker.app.controller.WorkoutController;
import com.gymtracker.app.dto.request.WorkoutCreationRequest;
import com.gymtracker.app.dto.request.WorkoutItemDTO;
import com.gymtracker.app.dto.request.WorkoutRepetitionItemDTO;
import com.gymtracker.app.mapper.ExerciseMapperImpl;
import com.gymtracker.app.mapper.TrainingPlanItemMapperImpl;
import com.gymtracker.app.mapper.TrainingPlanMapperImpl;
import com.gymtracker.app.mapper.WorkoutItemMapperImpl;
import com.gymtracker.app.mapper.WorkoutMapperImpl;
import com.gymtracker.app.security.JwtAuthenticationFilter;
import com.gymtracker.app.service.TrainingPlanService;
import com.gymtracker.app.service.WorkoutService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = WorkoutController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({WorkoutMapperImpl.class, WorkoutItemMapperImpl.class, ExerciseMapperImpl.class, TrainingPlanMapperImpl.class, TrainingPlanItemMapperImpl.class})
class WorkoutControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutService workoutService;

    @MockitoBean
    private TrainingPlanService trainingPlanService;

    @MockitoBean
    private JwtAuthenticationFilter jwtService;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(objectMapper);
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenCreateWorkoutRequest_whenCreateWorkoutCalled_thenReturnCreatedStatus() throws Exception{
        List<WorkoutRepetitionItemDTO.ExerciseSet> sets = List.of(
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(10)
                        .weight(BigDecimal.valueOf(50.0))
                        .build(),
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(8)
                        .weight(BigDecimal.valueOf(55.0))
                        .build(),
                WorkoutRepetitionItemDTO.ExerciseSet.builder()
                        .reps(6)
                        .weight(BigDecimal.valueOf(60.0))
                        .build()
        );

        List<WorkoutItemDTO> workoutItems = List.of(
                WorkoutRepetitionItemDTO.builder()
                        .exerciseId(1L)
                        .sets(sets)
                        .build()
        );

        WorkoutCreationRequest workoutCreationRequest = WorkoutCreationRequest.builder()
                .trainingId(1L)
                .workoutItems(workoutItems)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/workouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(workoutCreationRequest)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenValidParameters_whenGetWorkoutExerciseHistoryCalled_thenReturnOkStatus() throws Exception {
        Long exerciseId = 1L;
        int limit = 5;

        mockMvc.perform(MockMvcRequestBuilders.get("/workouts/exercises/{exerciseId}/history", exerciseId)
                        .param("limit", String.valueOf(limit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenValidParameters_whenGetWorkoutExerciseHistoryByWorkoutInPeriod_shouldReturnOkStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/workouts/exercises/{exerciseId}/history/period", 1L)
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenValidParameters_whenGetWorkoutTrainingHistory_shouldReturnOkStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/workouts/trainings/{trainingId}/history/period", 1L)
                        .param("startDate", "2023-01-01")
                        .param("endDate", "2023-12-31")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenRequest_whenGetUserWorkoutsCalled_shouldReturnOkStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/workouts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

}
