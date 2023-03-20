package com.workshop.recipe.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.recipe.exception.ExceptionHandlerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@SpringBootTest
public class AuthenticationControllerTests {
    @Mock
    private AuthenticationService mockAuthenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Autowired
    private MockMvc mockMvcController;

    private ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        mockMvcController = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new ExceptionHandlerController()).build();
        mapper = new ObjectMapper();
    }

    @Test
    public void testLoginSuccessfully() throws Exception{
        AuthenticationRequest req = new AuthenticationRequest().builder()
                .email("james@gmail.com")
                .password("123456")
                .build();

        AuthenticationResponse res = new AuthenticationResponse();

        when(mockAuthenticationService.authenticate(req)).thenReturn(res);

        this.mockMvcController.perform(
                        MockMvcRequestBuilders.post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(mockAuthenticationService, times(1)).authenticate(req);
    }
}
