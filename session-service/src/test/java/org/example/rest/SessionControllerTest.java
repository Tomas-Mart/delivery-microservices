package org.example.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rest.dto.SessionDto;
import org.example.rest.dto.StartSessionRequest;
import org.example.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionServiceImpl sessionServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void startSession_ShouldReturnCreated() throws Exception {
        // given
        StartSessionRequest request = new StartSessionRequest();
        request.setCourierId("test-courier");

        // Создаём DTO без builder
        SessionDto sessionDto = new SessionDto();
        sessionDto.setId(1L);
        sessionDto.setCourierId("test-courier");
        sessionDto.setStatus("ACTIVE");

        when(sessionServiceImpl.start(anyString()))
                .thenReturn(sessionDto);

        // when/then
        mockMvc.perform(post("/api/sessions/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.courierId").value("test-courier"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}