package com.SinistraPro.domain.infrastructure.persistence.mapper;


import com.SinistraPro.domain.model.Rapport;
import com.SinistraPro.domain.infrastructure.persistence.entity.RapportEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RapportMapper {

    private final UtilisateurMapper utilisateurMapper;

    public Rapport toDomain(RapportEntity entity) {
        if (entity == null) return null;
        return Rapport.builder()
                .id(entity.getId())
                .descriptionDommages(entity.getDescriptionDommages())
                .montantEstime(entity.getMontantEstime())
                .observations(entity.getObservations())
                .dateSoumission(entity.getDateSoumission())
                .expert(utilisateurMapper.toDomain(entity.getExpert()))
                .build();
    }

    public RapportEntity toEntity(Rapport domain) {
        if (domain == null) return null;
        return RapportEntity.builder()
                .id(domain.getId())
                .descriptionDommages(domain.getDescriptionDommages())
                .montantEstime(domain.getMontantEstime())
                .observations(domain.getObservations())
                .dateSoumission(domain.getDateSoumission())
                .expert(utilisateurMapper.toEntity(domain.getExpert()))
                .build();
    }
}
