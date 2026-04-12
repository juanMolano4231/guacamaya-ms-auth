package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.*;
import com.example.demo.entity.dto.AuthResponse;
import com.example.demo.entity.dto.LoginRequest;
import com.example.demo.entity.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshService;
    private final UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthService(JwtService jwtService,
            RefreshTokenService refreshService,
            UserRepository userRepo) {
        this.jwtService = jwtService;
        this.refreshService = refreshService;
        this.userRepo = userRepo;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("USER");

        userRepo.save(user);

        String access = jwtService.generateAccessToken(user);
        RefreshToken refresh = refreshService.create(user);

        return new AuthResponse(access, refresh.getToken());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepo.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshService.create(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshToken rt = refreshService.verify(refreshToken);
        User user = rt.getUser();

        RefreshToken newRt = refreshService.rotate(rt);

        String newAccess = jwtService.generateAccessToken(user);

        return new AuthResponse(newAccess, newRt.getToken());
    }

    

}