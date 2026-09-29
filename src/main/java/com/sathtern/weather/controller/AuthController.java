package com.sathtern.weather.controller;

import com.sathtern.weather.dto.*;
import com.sathtern.weather.model.RefreshToken;
import com.sathtern.weather.model.User;
import com.sathtern.weather.repository.RefreshTokenRepository;
import com.sathtern.weather.repository.UserRepository;
import com.sathtern.weather.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          RefreshTokenRepository refreshTokenRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Cet email est déjà utilisé"));
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Utilisateur créé avec succès"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        Optional<User> userOpt = userRepository.findByEmail(dto.getEmail());

        if (userOpt.isEmpty() || !passwordEncoder.matches(dto.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Email ou mot de passe incorrect"));
        }

        User user = userOpt.get();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshToken);

        return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshTokenValue));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshDto dto) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(dto.getRefreshToken());

        if (tokenOpt.isEmpty() || tokenOpt.get().getIsRevoked()
                || tokenOpt.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(401).body(Map.of("message", "Refresh token invalide ou expiré"));
        }

        User user = tokenOpt.get().getUser();
        String newAccessToken = jwtService.generateAccessToken(user);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody RefreshDto dto) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(dto.getRefreshToken());

        tokenOpt.ifPresent(token -> {
            token.setIsRevoked(true);
            refreshTokenRepository.save(token);
        });

        return ResponseEntity.ok(Map.of("message", "Déconnecté avec succès"));
    }
}