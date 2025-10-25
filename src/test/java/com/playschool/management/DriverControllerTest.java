package com.playschool.management;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playschool.management.controller.DriverController;
import com.playschool.management.dto.request.MinimalDriverRequestDTO;
import com.playschool.management.entity.Driver;
import com.playschool.management.service.DriverService;

@WebMvcTest(controllers = DriverController.class)
@AutoConfigureMockMvc(addFilters = false)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverService driverService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("COPILOT-FIX: returns 201 when driver profile is auto-created")
    @WithMockUser(roles = "DRIVER")
    void createMinimalDriver_createsNewDriverWhenMissing() throws Exception {
        MinimalDriverRequestDTO request = new MinimalDriverRequestDTO();
        request.setUserId("42");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail("test.user@example.com");

        Driver created = new Driver();
        created.setId("driver-42");
        created.setUserId("42");
        created.setFirstName("Test");
        created.setLastName("User");
        created.setEmail("test.user@example.com");
        created.setPhoneNumber("AUTO-42");
        created.setProfilePhoto("https://ui-avatars.com/api/?background=2563eb&color=fff&name=TU");
        created.setBloodGroup("UNKNOWN");
        created.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        created.setPassword("AUTO-secret");

        when(driverService.findByUserId("42")).thenReturn(Optional.empty());
        when(driverService.createMinimalDriver(any(MinimalDriverRequestDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/transport/drivers/minimal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("42"));
    }

    @Test
    @DisplayName("COPILOT-FIX: returns existing driver when record already present")
    @WithMockUser(roles = "DRIVER")
    void createMinimalDriver_returnsExistingDriverWhenFound() throws Exception {
        MinimalDriverRequestDTO request = new MinimalDriverRequestDTO();
        request.setUserId("77");
        request.setFirstName("Existing");
        request.setLastName("Driver");

        Driver existing = new Driver();
        existing.setId("driver-77");
        existing.setUserId("77");
        existing.setFirstName("Existing");
        existing.setLastName("Driver");
        existing.setEmail("existing.driver@example.com");
        existing.setPhoneNumber("AUTO-77");
        existing.setProfilePhoto("https://ui-avatars.com/api/?background=2563eb&color=fff&name=ED");
        existing.setBloodGroup("UNKNOWN");
        existing.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        existing.setPassword("AUTO-secret");

        when(driverService.findByUserId("77")).thenReturn(Optional.of(existing));

        mockMvc.perform(post("/api/transport/drivers/minimal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("77"));
    }
}
