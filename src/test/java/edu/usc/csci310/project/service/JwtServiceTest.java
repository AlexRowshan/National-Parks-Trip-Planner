package edu.usc.csci310.project.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        jwtService.secret = "mysecret"; // Set the secret key value directly
    }

    @Test
    void testGetJWT() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String jwt = jwtService.getJWT(userDetails);
        assertNotNull(jwt);
    }

    @Test
    void testIsValidToken() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String jwt = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 360000))
                .signWith(SignatureAlgorithm.HS256, "mysecret")
                .compact();

        assertTrue(jwtService.isValidToken(jwt, userDetails));
    }

    @Test
    void testIsValidToken_InvalidUsername() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String jwt = Jwts.builder()
                .setSubject("invaliduser")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 360000))
                .signWith(SignatureAlgorithm.HS256, "mysecret")
                .compact();

        assertFalse(jwtService.isValidToken(jwt, userDetails));
    }

    @Test
    void testIsValidToken_ExpiredToken() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String jwt = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis() - 360001))
                .setExpiration(new Date(System.currentTimeMillis() - 1))
                .signWith(SignatureAlgorithm.HS256, "mysecret")
                .compact();

        ExpiredJwtException exception = assertThrows(ExpiredJwtException.class, () -> jwtService.isValidToken(jwt, userDetails));

        assertNotNull(exception);
    }

    @Test
    void testExtractClaim() {
        String jwt = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 360000))
                .signWith(SignatureAlgorithm.HS256, "mysecret")
                .compact();

        String subject = jwtService.extractClaim(jwt, Claims::getSubject);
        assertEquals("testuser", subject);

        Date expiration = jwtService.extractClaim(jwt, Claims::getExpiration);
        assertNotNull(expiration);
    }
}