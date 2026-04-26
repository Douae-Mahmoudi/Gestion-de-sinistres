package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    public Utilisateur toDomain(UtilisateurEntity entity) {
        if (entity == null) return null;
        return Utilisateur.builder()
                .id(entity.getId())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .email(entity.getEmail())
                .motDePasse(entity.getMotDePasse())
                .telephone(entity.getTelephone())
                .role(entity.getRole())
                .dateCreation(entity.getDateCreation())
                .resetToken(entity.getResetToken())
                .resetTokenExpiry(entity.getResetTokenExpiry())
                .build();
    }

    public UtilisateurEntity toEntity(Utilisateur domain) {
        if (domain == null) return null;
        return UtilisateurEntity.builder()
                .id(domain.getId())
                .nom(domain.getNom())
                .prenom(domain.getPrenom())
                .email(domain.getEmail())
                .motDePasse(domain.getMotDePasse())
                .telephone(domain.getTelephone())
                .role(domain.getRole())
                .dateCreation(domain.getDateCreation())
                .resetToken(domain.getResetToken())
                .resetTokenExpiry(domain.getResetTokenExpiry())
                .build();
    }
}