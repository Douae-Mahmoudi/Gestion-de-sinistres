package com.SinistraPro.domain.infrastructure.persistence.mapper;


import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SinistreMapper {

    private final UtilisateurMapper utilisateurMapper;
    private final RapportMapper rapportMapper;
    private final DecisionMapper decisionMapper;

    public Sinistre toDomain(SinistreEntity entity) {
        if (entity == null) return null;
        return Sinistre.builder()
                .id(entity.getId())
                .numero(entity.getNumero())
                .typeSinistre(entity.getTypeSinistre())
                .description(entity.getDescription())
                .dateIncident(entity.getDateIncident())
                .lieuIncident(entity.getLieuIncident())
                .numeroPolicAssurance(entity.getNumeroPolicAssurance())
                .numeroConstatAmiable(entity.getNumeroConstatAmiable())
                .statut(entity.getStatut())
                .dateDeclaration(entity.getDateDeclaration())
                .client(utilisateurMapper.toDomain(entity.getClient()))
                .agent(utilisateurMapper.toDomain(entity.getAgent()))
                .expert(utilisateurMapper.toDomain(entity.getExpert()))
                .rapport(rapportMapper.toDomain(entity.getRapport()))
                .decision(decisionMapper.toDomain(entity.getDecision()))
                .build();
    }

    public SinistreEntity toEntity(Sinistre domain) {
        if (domain == null) return null;
        return SinistreEntity.builder()
                .id(domain.getId())
                .numero(domain.getNumero())
                .typeSinistre(domain.getTypeSinistre())
                .description(domain.getDescription())
                .dateIncident(domain.getDateIncident())
                .lieuIncident(domain.getLieuIncident())
                .numeroPolicAssurance(domain.getNumeroPolicAssurance())
                .numeroConstatAmiable(domain.getNumeroConstatAmiable())
                .statut(domain.getStatut())
                .dateDeclaration(domain.getDateDeclaration())
                .client(utilisateurMapper.toEntity(domain.getClient()))
                .agent(utilisateurMapper.toEntity(domain.getAgent()))
                .expert(utilisateurMapper.toEntity(domain.getExpert()))
                .rapport(rapportMapper.toEntity(domain.getRapport()))
                .decision(decisionMapper.toEntity(domain.getDecision()))
                .build();
    }
}
