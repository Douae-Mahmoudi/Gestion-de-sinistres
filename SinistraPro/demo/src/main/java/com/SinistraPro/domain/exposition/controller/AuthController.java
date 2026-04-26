package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.application.service.PasswordResetService;
import com.SinistraPro.domain.exposition.dto.request.ForgotPasswordRequest;
import com.SinistraPro.domain.exposition.dto.request.ResetPasswordRequest;
import com.SinistraPro.domain.exposition.dto.request.VerifierCodeRequest;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import com.SinistraPro.domain.exposition.dto.request.LoginRequest;
import com.SinistraPro.domain.exposition.dto.request.RegisterRequest;
import com.SinistraPro.domain.exposition.dto.response.AuthResponse;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PasswordResetService passwordResetService;
    private final UtilisateurRepositoryPort utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .telephone(request.getTelephone())
                .role(Role.CLIENT)
                .dateCreation(LocalDateTime.now())
                .build();

        Utilisateur saved = utilisateurRepository.save(utilisateur);

        String token = jwtService.generateToken(
                saved.getEmail(), saved.getRole().name());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponse.builder()
                        .token(token)
                        .email(saved.getEmail())
                        .role(saved.getRole().name())
                        .nomComplet(saved.getNomComplet())
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (utilisateur == null ||
                !passwordEncoder.matches(
                        request.getMotDePasse(),
                        utilisateur.getMotDePasse())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(
                utilisateur.getEmail(), utilisateur.getRole().name());

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .token(token)
                        .email(utilisateur.getEmail())
                        .role(utilisateur.getRole().name())
                        .nomComplet(utilisateur.getNomComplet())
                        .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            passwordResetService.demanderReset(request.getEmail());
            return ResponseEntity.ok(
                    "Code de vérification envoyé à : " + request.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(
            @Valid @RequestBody VerifierCodeRequest request) {
        try {
            passwordResetService.verifierCode(
                    request.getEmail(),
                    request.getCode());
            return ResponseEntity.ok("Code valide");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            passwordResetService.reinitialiserMotDePasse(
                    request.getEmail(),
                    request.getCode(),
                    request.getNouveauMotDePasse());
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}