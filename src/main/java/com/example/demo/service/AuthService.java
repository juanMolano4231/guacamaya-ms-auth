package com.example.demo.service;

import com.example.demo.entity.RefreshToken;
import com.example.demo.entity.User;
import com.example.demo.entity.dto.LoginRequest;
import com.example.demo.entity.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import com.example.exception.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final RefreshTokenService refreshService;
    private final UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthService(JwtService jwtService, RefreshTokenService refreshService, UserRepository userRepo) {
        this.jwtService = jwtService;
        this.refreshService = refreshService;
        this.userRepo = userRepo;
    }

    public void register(RegisterRequest req, HttpServletResponse response) {
        if (userRepo.existsByUsername(req.getUsername())) {
            throw new AuthException("Username already exists");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setRole("USER");

        userRepo.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshService.create(user);

        setTokensInCookies(response, accessToken, refreshToken.getToken());
    }

    public void login(LoginRequest req, HttpServletResponse response) {
        User user = userRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshService.create(user);

        setTokensInCookies(response, accessToken, refreshToken.getToken());
    }

    public void refresh(String refreshToken, HttpServletResponse response) {
        RefreshToken rt = refreshService.verify(refreshToken);
        User user = rt.getUser();

        RefreshToken newRefresh = refreshService.rotate(rt);
        String newAccess = jwtService.generateAccessToken(user);

        setTokensInCookies(response, newAccess, newRefresh.getToken());
    }

    private void setTokensInCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false); // set true in production with HTTPS
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 15); // 15 minutes

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 30); // 30 days

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

}