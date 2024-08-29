package edu.usc.csci310.project.config;

import edu.usc.csci310.project.repository.UserRepository;
import edu.usc.csci310.project.service.JwtService;
import edu.usc.csci310.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @MockBean
    private UserService userDetailsServiceImp;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private SecurityConfig securityConfig;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSecurityFilterChain() throws Exception {
        // Create a mock HttpSecurity object
        HttpSecurity http = mock(HttpSecurity.class);

        // Define the behavior of the mocked HttpSecurity object
        CorsConfigurer corsConfigurer = mock(CorsConfigurer.class);
        when(http.cors()).thenReturn(corsConfigurer);
        when(corsConfigurer.and()).thenReturn(http);

        // Define the behavior for other methods of HttpSecurity
        when(http.csrf(any())).thenReturn(http);
        when(http.authorizeHttpRequests(any())).thenReturn(http);
        when(http.userDetailsService(any())).thenReturn(http);
        when(http.sessionManagement(any())).thenReturn(http);
        when(http.addFilterBefore(any(), any())).thenReturn(http);
        when(http.exceptionHandling(any())).thenReturn(http);
        when(http.logout(any())).thenReturn(http);
        when(http.requiresChannel(any())).thenReturn(http);
        when(http.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

        // Call the securityFilterChain method
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(http);

        // Verify that the returned SecurityFilterChain is not null
        assertThat(filterChain).isNotNull();
    }

    @Test
    void passwordEncoder_shouldReturnNoOpPasswordEncoder() {
        // Act
        PasswordEncoder result = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(result);
        assertInstanceOf(BCryptPasswordEncoder.class, result);
    }
}