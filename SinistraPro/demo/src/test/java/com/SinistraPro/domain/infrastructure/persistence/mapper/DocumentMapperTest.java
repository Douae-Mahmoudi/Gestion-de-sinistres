package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.infrastructure.persistence.entity.DocumentEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Document;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.StatutSinistre;
import com.SinistraPro.domain.model.Utilisateur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentMapperTest {

    @Mock
    private UtilisateurMapper utilisateurMapper;

    @InjectMocks
    private DocumentMapper documentMapper;


    private UtilisateurEntity buildUploaderEntity() {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setId(5L);
        u.setNom("Client"); u.setPrenom("A");
        u.setEmail("client@mail.ma");
        u.setRole(Role.CLIENT);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return u;
    }

    private Utilisateur buildUploaderDomain() {
        return Utilisateur.builder()
                .id(5L)
                .nom("Client").prenom("A")
                .email("client@mail.ma")
                .role(Role.CLIENT)
                .build();
    }

    private SinistreEntity buildSinistreEntity() {
        SinistreEntity s = new SinistreEntity();
        s.setId(20L);
        s.setNumero("SIN-001");
        s.setStatut(StatutSinistre.DECLARE);
        s.setTypeSinistre("ACCIDENT");
        s.setDateIncident(LocalDate.now());
        s.setDateDeclaration(LocalDateTime.now());
        return s;
    }

    private DocumentEntity buildDocumentEntity(UtilisateurEntity uploader) {
        DocumentEntity d = new DocumentEntity();
        d.setId(1L);
        d.setNomFichier("facture.pdf");
        d.setCheminFichier("/uploads/facture.pdf");
        d.setTypeDocument("FACTURE");
        d.setTaille(1024L);
        d.setDateUpload(LocalDateTime.of(2024, 5, 1, 9, 0));
        d.setUploadePar(uploader);
        return d;
    }

    private Document buildDocumentDomain(Utilisateur uploader) {
        return Document.builder()
                .id(1L)
                .nomFichier("facture.pdf")
                .cheminFichier("/uploads/facture.pdf")
                .typeDocument("FACTURE")
                .taille(1024L)
                .dateUpload(LocalDateTime.of(2024, 5, 1, 9, 0))
                .uploadePar(uploader)
                .build();
    }


    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("Devrait mapper tous les champs de l'entity vers le domain")
        void shouldMapAllFields_FromEntityToDomain() {
            UtilisateurEntity uploaderEntity = buildUploaderEntity();
            Utilisateur uploaderDomain = buildUploaderDomain();
            DocumentEntity entity = buildDocumentEntity(uploaderEntity);

            when(utilisateurMapper.toDomain(uploaderEntity)).thenReturn(uploaderDomain);

            Document result = documentMapper.toDomain(entity);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNomFichier()).isEqualTo("facture.pdf");
            assertThat(result.getCheminFichier()).isEqualTo("/uploads/facture.pdf");
            assertThat(result.getTypeDocument()).isEqualTo("FACTURE");
            assertThat(result.getTaille()).isEqualTo(1024L);
            assertThat(result.getDateUpload()).isEqualTo(LocalDateTime.of(2024, 5, 1, 9, 0));
            assertThat(result.getUploadePar()).isEqualTo(uploaderDomain);

            verify(utilisateurMapper).toDomain(uploaderEntity);
        }

        @Test
        @DisplayName("Devrait retourner null si l'entity est null")
        void shouldReturnNull_WhenEntityIsNull() {
            Document result = documentMapper.toDomain(null);

            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper);
        }
    }


    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("Devrait mapper tous les champs du domain vers l'entity avec le sinistre")
        void shouldMapAllFields_FromDomainToEntity() {
            Utilisateur uploaderDomain = buildUploaderDomain();
            UtilisateurEntity uploaderEntity = buildUploaderEntity();
            SinistreEntity sinistreEntity = buildSinistreEntity();
            Document domain = buildDocumentDomain(uploaderDomain);

            when(utilisateurMapper.toEntity(uploaderDomain)).thenReturn(uploaderEntity);

            DocumentEntity result = documentMapper.toEntity(domain, sinistreEntity);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNomFichier()).isEqualTo("facture.pdf");
            assertThat(result.getCheminFichier()).isEqualTo("/uploads/facture.pdf");
            assertThat(result.getTypeDocument()).isEqualTo("FACTURE");
            assertThat(result.getTaille()).isEqualTo(1024L);
            assertThat(result.getSinistre()).isEqualTo(sinistreEntity);
            assertThat(result.getUploadePar()).isEqualTo(uploaderEntity);

            verify(utilisateurMapper).toEntity(uploaderDomain);
        }

        @Test
        @DisplayName("Devrait retourner null si le domain est null")
        void shouldReturnNull_WhenDomainIsNull() {
            DocumentEntity result = documentMapper.toEntity(null, buildSinistreEntity());

            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper);
        }

        @Test
        @DisplayName("Devrait lier correctement le sinistre passé en paramètre")
        void shouldLinkSinistre_PassedAsParameter() {
            Utilisateur uploaderDomain = buildUploaderDomain();
            SinistreEntity sinistreEntity = buildSinistreEntity();
            Document domain = buildDocumentDomain(uploaderDomain);

            when(utilisateurMapper.toEntity(uploaderDomain)).thenReturn(buildUploaderEntity());

            DocumentEntity result = documentMapper.toEntity(domain, sinistreEntity);

            assertThat(result.getSinistre().getId()).isEqualTo(20L);
            assertThat(result.getSinistre().getNumero()).isEqualTo("SIN-001");
        }
    }
}