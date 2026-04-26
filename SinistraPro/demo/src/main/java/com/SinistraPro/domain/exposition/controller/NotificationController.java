package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.application.service.NotificationService;
import com.SinistraPro.domain.exposition.dto.response.NotificationResponse;
import com.SinistraPro.domain.model.Notification;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UtilisateurRepositoryPort utilisateurRepository;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMesNotifications(Authentication auth) {
        Utilisateur user = getUser(auth);
        List<NotificationResponse> result = notificationService
                .getMesNotifications(user.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/non-lues/count")
    public ResponseEntity<Map<String, Long>> countNonLues(Authentication auth) {
        Utilisateur user = getUser(auth);
        return ResponseEntity.ok(Map.of("count",
                notificationService.compterNonLues(user.getId())));
    }

    @PutMapping("/{id}/lire")
    public ResponseEntity<Void> marquerLue(@PathVariable Long id) {
        notificationService.marquerCommeLue(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/lire-tout")
    public ResponseEntity<Void> marquerToutesLues(Authentication auth) {
        Utilisateur user = getUser(auth);
        notificationService.marquerToutesCommeLues(user.getId());
        return ResponseEntity.ok().build();
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType())
                .lue(n.isLue())
                .dateCreation(n.getDateCreation())
                .sinistreId(n.getSinistre() != null ? n.getSinistre().getId() : null)
                .build();
    }

    private Utilisateur getUser(Authentication auth) {
        return utilisateurRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }
}