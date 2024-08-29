package edu.usc.csci310.project.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
class CorsConfigTest {

    @Mock
    private CorsRegistry registry;

    @Mock
    private CorsRegistration corsRegistration;

    @InjectMocks
    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCorsMappings_shouldConfigureCorsRegistry() {
        // Arrange
        when(registry.addMapping(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods(any(String[].class))).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders(anyString())).thenReturn(corsRegistration);
        when(corsRegistration.allowCredentials(anyBoolean())).thenReturn(corsRegistration);
        when(corsRegistration.maxAge(anyLong())).thenReturn(corsRegistration);

        // Act
        corsConfig.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping(eq("/**"));
        verify(corsRegistration).allowedOrigins(eq("http://localhost:8080"));
        verify(corsRegistration).allowedMethods(eq("GET"), eq("POST"), eq("PUT"), eq("DELETE"), eq("OPTIONS"), eq("PATCH"));
        verify(corsRegistration).allowedHeaders(eq("*"));
        verify(corsRegistration).allowCredentials(eq(true));
        verify(corsRegistration).maxAge(eq(3600L));
    }
}