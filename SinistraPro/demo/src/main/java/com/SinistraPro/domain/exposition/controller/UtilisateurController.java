package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import com.SinistraPro.domain.exposition.dto.request.UpdateProfilRequest;
import com.SinistraPro.domain.exposition.dto.request.ChangePasswordRequest;
import com.SinistraPro.domain.exposition.dto.response.UtilisateurResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurRepositoryPort utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<UtilisateurResponse> getMonProfil(Authentication auth) {
        Utilisateur user = getUser(auth);
        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UtilisateurResponse> updateProfil(
            @Valid @RequestBody UpdateProfilRequest request,
            Authentication auth) {

        Utilisateur user = getUser(auth);
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setTelephone(request.getTelephone());
        user.setAdresse(request.getAdresse());
        Utilisateur saved = utilisateurRepository.save(user);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PutMapping("/me/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication auth) {

        Utilisateur user = getUser(auth);

        if (!passwordEncoder.matches(request.getAncienMotDePasse(), user.getMotDePasse())) {
            return ResponseEntity.badRequest().body("Ancien mot de passe incorrect");
        }

        user.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        utilisateurRepository.save(user);
        return ResponseEntity.ok("Mot de passe modifié avec succès");
    }

    private Utilisateur getUser(Authentication auth) {
        return utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }

    private UtilisateurResponse toResponse(Utilisateur u) {
        return UtilisateurResponse.builder()
                .id(u.getId())
                .nom(u.getNom())
                .prenom(u.getPrenom())
                .email(u.getEmail())
                .telephone(u.getTelephone())
                .adresse(u.getAdresse())
                .role(u.getRole().name())
                .dateCreation(u.getDateCreation())
                .build();
    }
    @GetMapping("/experts")
    public ResponseEntity<List<UtilisateurResponse>> getExperts() {
        return ResponseEntity.ok(
                utilisateurRepository.findByRole(Role.EXPERT)
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList())
        );
    }
}