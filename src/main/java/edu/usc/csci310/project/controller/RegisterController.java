package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.entity.UserEntity;
import edu.usc.csci310.project.repository.UserRepository;
import edu.usc.csci310.project.service.JwtService;
import edu.usc.csci310.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    public UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/api/createAccount")
    public ResponseEntity<String> registerUser(@RequestBody UserEntity userEntity) {
        // base 64 encode the username
        userEntity.setUsername(Base64.getEncoder().encodeToString(userEntity.getUsername().getBytes()));
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        UserEntity createdUserEntity = userService.createUser(userEntity);

        if (createdUserEntity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username taken");
        } else {
            UserDetails userDetails = userService.loadUserByUsername(userEntity.getUsername());
            final String jwt = jwtService.getJWT(userDetails);
            return ResponseEntity.ok().body(jwt);
        }
    }

    @DeleteMapping("/api/delete/{username}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> deleteUser(@PathVariable String username)
    {
        if(userService.deleteUser(username))
        {
            return ResponseEntity.ok().body("Successfully delete user");
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("UserEntity not found");
        }
    }
}