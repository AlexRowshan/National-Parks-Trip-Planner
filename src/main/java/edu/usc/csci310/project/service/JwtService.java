package edu.usc.csci310.project.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${secretKey}")
    public String secret;

    public String getJWT(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1440000))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public Boolean isValidToken(String token, UserDetails userDetails){
        final String userName = extractClaim(token, Claims::getSubject);
        return (userName.equals(userDetails.getUsername()) && !extractClaim(token, Claims::getExpiration).before(new Date()));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        claimsResolver.apply(claims);
        return claimsResolver.apply(claims);
    }
}