package com.outgo.api.web.auth;

import com.outgo.api.infrastructure.security.JwtService;
import com.outgo.api.infrastructure.security.UserJpaEntity;
import com.outgo.api.infrastructure.security.UserJpaRepository;
import com.outgo.api.web.auth.dto.AuthResponse;
import com.outgo.api.web.auth.dto.LoginRequest;
import com.outgo.api.web.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserJpaRepository userJpaRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userJpaRepository = userJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userJpaRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        UUID userId = UUID.randomUUID();
        UserJpaEntity user = new UserJpaEntity(
                userId,
                request.email(),
                passwordEncoder.encode(request.password()),
                Instant.now());
        userJpaRepository.save(user);
        String token = jwtService.generateToken(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, userId));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserJpaEntity user = userJpaRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        String token = jwtService.generateToken(user.getId());
        return ResponseEntity.ok(new AuthResponse(token, user.getId()));
    }
}
