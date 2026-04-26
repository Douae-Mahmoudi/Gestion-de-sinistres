package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Notification;
import com.SinistraPro.domain.infrastructure.persistence.entity.NotificationEntity;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.model.Sinistre;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toDomain(NotificationEntity e) {
        if (e == null) return null;

        Utilisateur u = new Utilisateur();
        u.setId(e.getUtilisateur().getId());

        Sinistre s = null;
        if (e.getSinistre() != null) {
            s = new Sinistre();
            s.setId(e.getSinistre().getId());
        }

        return Notification.builder()
                .id(e.getId())
                .message(e.getMessage())
                .type(e.getType())
                .lue(e.isLue())
                .dateCreation(e.getDateCreation())
                .utilisateur(u)
                .sinistre(s)
                .build();
    }

    public NotificationEntity toEntity(Notification n,
                                       UtilisateurEntity utilisateurEntity,
                                       SinistreEntity sinistreEntity) {
        return NotificationEntity.builder()
                .id(n.getId())
                .message(n.getMessage())
                .type(n.getType())
                .lue(n.isLue())
                .dateCreation(n.getDateCreation())
                .utilisateur(utilisateurEntity)
                .sinistre(sinistreEntity)
                .build();
    }
}