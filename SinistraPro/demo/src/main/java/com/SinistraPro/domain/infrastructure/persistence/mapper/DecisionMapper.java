package com.SinistraPro.domain.infrastructure.persistence.mapper;


import com.SinistraPro.domain.model.Decision;
import com.SinistraPro.domain.infrastructure.persistence.entity.DecisionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecisionMapper {

    private final UtilisateurMapper utilisateurMapper;

    public Decision toDomain(DecisionEntity entity) {
        if (entity == null) return null;
        return Decision.builder()
                .id(entity.getId())
                .montantFinal(entity.getMontantFinal())
                .statut(entity.getStatut())
                .motif(entity.getMotif())
                .dateDecision(entity.getDateDecision())
                .numeroVirement(entity.getNumeroVirement())
                .datePaiement(entity.getDatePaiement())
                .superviseur(utilisateurMapper.toDomain(entity.getSuperviseur()))
                .build();
    }

    public DecisionEntity toEntity(Decision domain) {
        if (domain == null) return null;
        return DecisionEntity.builder()
                .id(domain.getId())
                .montantFinal(domain.getMontantFinal())
                .statut(domain.getStatut())
                .motif(domain.getMotif())
                .dateDecision(domain.getDateDecision())
                .numeroVirement(domain.getNumeroVirement())
                .datePaiement(domain.getDatePaiement())
                .superviseur(utilisateurMapper.toEntity(domain.getSuperviseur()))
                .build();
    }
}