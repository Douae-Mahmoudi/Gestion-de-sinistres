package com.SinistraPro.domain.infrastructure.persistence.mapper;


import com.SinistraPro.domain.model.Document;
import com.SinistraPro.domain.infrastructure.persistence.entity.DocumentEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentMapper {

    private final UtilisateurMapper utilisateurMapper;

    public Document toDomain(DocumentEntity entity) {
        if (entity == null) return null;
        return Document.builder()
                .id(entity.getId())
                .nomFichier(entity.getNomFichier())
                .cheminFichier(entity.getCheminFichier())
                .typeDocument(entity.getTypeDocument())
                .taille(entity.getTaille())
                .dateUpload(entity.getDateUpload())
                .uploadePar(utilisateurMapper.toDomain(entity.getUploadePar()))
                .build();
    }


    public DocumentEntity toEntity(Document domain, SinistreEntity sinistreEntity) {
        if (domain == null) return null;
        return DocumentEntity.builder()
                .id(domain.getId())
                .nomFichier(domain.getNomFichier())
                .cheminFichier(domain.getCheminFichier())
                .typeDocument(domain.getTypeDocument())
                .taille(domain.getTaille())
                .dateUpload(domain.getDateUpload())
                .uploadePar(utilisateurMapper.toEntity(domain.getUploadePar()))
                .sinistre(sinistreEntity)
                .build();
    }
}
