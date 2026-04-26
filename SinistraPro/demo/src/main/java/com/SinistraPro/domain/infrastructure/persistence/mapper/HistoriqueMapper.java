package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.model.Historique;
import com.SinistraPro.domain.infrastructure.persistence.entity.HistoriqueEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistoriqueMapper {

    private final UtilisateurMapper utilisateurMapper;

    public Historique toDomain(HistoriqueEntity entity) {
        if (entity == null) return null;
        return Historique.builder()
                .id(entity.getId())
                .sinistreId(entity.getSinistre().getId())
                .ancienStatut(entity.getAncienStatut())
                .nouveauStatut(entity.getNouveauStatut())
                .commentaire(entity.getCommentaire())
                .effectuePar(utilisateurMapper.toDomain(entity.getEffectuePar()))
                .dateAction(entity.getDateAction())
                .build();
    }

    public HistoriqueEntity toEntity(Historique domain, SinistreEntity sinistreEntity) {
        if (domain == null) return null;
        return HistoriqueEntity.builder()
                .id(domain.getId())
                .sinistre(sinistreEntity)
                .ancienStatut(domain.getAncienStatut())
                .nouveauStatut(domain.getNouveauStatut())
                .commentaire(domain.getCommentaire())
                .effectuePar(utilisateurMapper.toEntity(domain.getEffectuePar()))
                .dateAction(domain.getDateAction())
                .build();
    }
}
