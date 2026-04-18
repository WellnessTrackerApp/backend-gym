package com.gymtracker.app.integration.exercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymtracker.app.controller.ExerciseController;
import com.gymtracker.app.domain.ExerciseCategory;
import com.gymtracker.app.dto.request.ExerciseCreationRequest;
import com.gymtracker.app.mapper.ExerciseMapperImpl;
import com.gymtracker.app.security.JwtAuthenticationFilter;
import com.gymtracker.app.service.ExerciseService;
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

@WebMvcTest(controllers = ExerciseController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ExerciseMapperImpl.class)
class ExerciseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExerciseService exerciseService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenExerciseData_whenCreateCustomExerciseCalled_shouldReturnCreatedResponse() throws Exception {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("My new exercise")
                .category(ExerciseCategory.UNCATEGORIZED)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseCreationRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenInvalidExerciseData_whenCreateCustomExerciseCalled_shouldReturnClientError() throws Exception {
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("A")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/exercises")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseCreationRequest)))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenValidRequest_whenGetUserExercisesCalled_shouldReturnOkResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exercises/user"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    void givenNoAuthentication_whenGetPredefinedExercisesCalled_shouldReturnOkResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/exercises"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenValidRequest_whenDeleteExerciseCalled_shouldReturnNoContentResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/exercises/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenValidRequest_whenUpdateExerciseCalled_shouldReturnOkResponse() throws Exception {
        long exerciseId = 1L;
        ExerciseCreationRequest exerciseCreationRequest = ExerciseCreationRequest.builder()
                .name("Updated exercise name")
                .category(ExerciseCategory.UNCATEGORIZED)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/exercises/" + exerciseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exerciseCreationRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
