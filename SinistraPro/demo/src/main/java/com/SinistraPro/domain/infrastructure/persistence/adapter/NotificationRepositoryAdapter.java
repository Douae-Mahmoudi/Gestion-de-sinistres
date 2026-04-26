package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.model.Notification;
import com.SinistraPro.domain.port.out.NotificationRepositoryPort;
import com.SinistraPro.domain.infrastructure.persistence.entity.NotificationEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.infrastructure.persistence.jpa.NotificationJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.jpa.SinistreJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.jpa.UtilisateurJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final NotificationJpaRepository notificationJpa;
    private final UtilisateurJpaRepository utilisateurJpa;
    private final SinistreJpaRepository sinistreJpa;
    private final NotificationMapper mapper;

    @Override
    public Notification save(Notification notification) {
        UtilisateurEntity u = utilisateurJpa.findById(notification.getUtilisateur().getId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        SinistreEntity s = null;
        if (notification.getSinistre() != null) {
            s = sinistreJpa.findById(notification.getSinistre().getId())
                    .orElse(null);
        }

        NotificationEntity saved = notificationJpa.save(mapper.toEntity(notification, u, s));
        return mapper.toDomain(saved);
    }

    @Override
    public List<Notification> findByUtilisateurId(Long utilisateurId) {
        return notificationJpa
                .findByUtilisateurIdOrderByDateCreationDesc(utilisateurId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countNonLuesByUtilisateurId(Long utilisateurId) {
        return notificationJpa.countByUtilisateurIdAndLueFalse(utilisateurId);
    }

    @Override
    public void marquerCommeLue(Long notificationId) {
        notificationJpa.marquerCommeLue(notificationId);
    }

    @Override
    public void marquerToutesCommeLues(Long utilisateurId) {
        notificationJpa.marquerToutesCommeLues(utilisateurId);
    }
}