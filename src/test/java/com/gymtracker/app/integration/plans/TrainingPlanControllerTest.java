package com.gymtracker.app.integration.plans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymtracker.app.controller.TrainingPlanController;
import com.gymtracker.app.dto.request.TrainingPlanCreationRequest;
import com.gymtracker.app.mapper.TrainingPlanMapper;
import com.gymtracker.app.security.JwtAuthenticationFilter;
import com.gymtracker.app.service.TrainingPlanService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingPlanController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingPlanControllerTest {
    @MockitoBean
    private TrainingPlanMapper trainingPlanMapper;

    @MockitoBean
    private TrainingPlanService trainingPlanService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenTrainingPlanData_whenCreateCustomTrainingPlanCalled_shouldReturnCreatedResponse() throws Exception {
        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planName("My Custom Plan")
                .planItems(List.of())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingPlanCreationRequest)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        Mockito.verify(trainingPlanService).generateCustomTrainingPlan(any(), any());
    }

    @Test
    void givenRequestToGetAllPredefinedTrainingPlans_whenGetAllPredefinedTrainingPlansCalled_shouldReturnOkResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/plans"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        Mockito.verify(trainingPlanService).getAllPredefinedTrainingPlans();
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenRequestToGetUserTrainingPlans_whenGetUserTrainingPlansCalled_shouldReturnOkResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/plans/user"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists());

        Mockito.verify(trainingPlanService).getUserTrainingPlans(any());
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenRequestToGetTrainingPlanById_whenGetTrainingPlanByIdCalled_shouldReturnOkResponse() throws Exception {
        String trainingPlanId = "1";

        mockMvc.perform(MockMvcRequestBuilders.get("/plans/" + trainingPlanId))
                .andExpect(status().isOk());

        Mockito.verify(trainingPlanService)
                .getTrainingPlanById(Long.parseLong(trainingPlanId), UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenRequestToDeleteTrainingPlanById_whenDeleteTrainingPlanByIdCalled_shouldReturnNoContentResponse() throws Exception {
        String trainingPlanId = "1";

        mockMvc.perform(MockMvcRequestBuilders.delete("/plans/" + trainingPlanId))
                .andExpect(status().isOk());

        Mockito.verify(trainingPlanService)
                .deleteTrainingPlan(Long.parseLong(trainingPlanId), UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    }

    @Test
    @WithMockUser(username = "123e4567-e89b-12d3-a456-426614174000")
    void givenRequestToUpdateTrainingPlanById_whenUpdateTrainingPlanByIdCalled_shouldReturnOkResponse() throws Exception {
        long trainingPlanId = 1L;
        TrainingPlanCreationRequest trainingPlanCreationRequest = TrainingPlanCreationRequest.builder()
                .planName("Updated Plan Name")
                .planItems(List.of())
                .build();

        mockMvc.perform(MockMvcRequestBuilders.put("/plans/" + trainingPlanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingPlanCreationRequest)))
                .andExpect(status().isOk());

        Mockito.verify(trainingPlanService)
                .updateCustomTrainingPlan(
                        trainingPlanCreationRequest,
                        UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                        trainingPlanId
                );
    }
}
