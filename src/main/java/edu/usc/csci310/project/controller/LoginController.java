package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.entity.UserEntity;
import edu.usc.csci310.project.service.JwtService;
import edu.usc.csci310.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Optional;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/api/login")
    public ResponseEntity<String> login(@RequestBody UserEntity userEntity)
    {
        userEntity.setUsername(Base64.getEncoder().encodeToString(userEntity.getUsername().getBytes()));
        Optional<UserEntity> getUser = userService.getUserByUsername(userEntity.getUsername());

        if(getUser.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("UserEntity not found.");
        }

        UserEntity foundUserEntity = getUser.get();
        if (!passwordEncoder.matches(userEntity.getPassword(), foundUserEntity.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect password");
        }

        UserDetails userDetails = userService.loadUserByUsername(userEntity.getUsername());
        final String jwt = jwtService.getJWT(userDetails);
        return ResponseEntity.ok().body(jwt);
    }
}