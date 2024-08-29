package edu.usc.csci310.project.filter;

import edu.usc.csci310.project.service.JwtService;
import edu.usc.csci310.project.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class JwtFilterTest {

    @Mock
    private UserService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternal_ValidAuthHeader_ShouldSetAuthentication() throws ServletException, IOException {
        String token = "validToken";
        String userName = "testUser";
        String requestPath = "/api/some-endpoint";

        when(request.getServletPath()).thenReturn(requestPath);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractClaim(eq(token), any())).thenReturn(userName);
        when(userDetailsService.loadUserByUsername(userName)).thenReturn(userDetails);
        when(jwtService.isValidToken(token, userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NonApiRequest_ShouldContinueFilterChain() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/non-api");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoAuthHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/some-endpoint");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidAuthHeader_ShouldContinueFilterChain() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/some-endpoint");
        when(request.getHeader("Authorization")).thenReturn("InvalidHeader");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_ShouldContinueFilterChain() throws ServletException, IOException {
        String token = "invalidToken";
        String userName = "testUser";
        when(request.getServletPath()).thenReturn("/api/some-endpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractClaim(token, Claims::getSubject)).thenReturn(userName);
        when(userDetailsService.loadUserByUsername(userName)).thenReturn(userDetails);
        when(jwtService.isValidToken(token, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_AuthenticationAlreadySet_ShouldContinueFilterChain() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/some-endpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
