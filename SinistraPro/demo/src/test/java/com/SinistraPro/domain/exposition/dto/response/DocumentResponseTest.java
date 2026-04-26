package com.SinistraPro.domain.exposition.dto.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentResponseTest {

    @Test
    @DisplayName("Succès : Devrait créer une DocumentResponse complète via le Builder")
    void shouldCreateDocumentResponseWithBuilder() {
        Long id = 42L;
        String nomFichier = "constat_amiable.pdf";
        String typeDoc = "CONSTAT";
        Long taille = 1024L;
        LocalDateTime maintenant = LocalDateTime.now();

        UtilisateurResponse uploader = UtilisateurResponse.builder()
                .nomComplet("Agent Dupont")
                .build();

        DocumentResponse response = DocumentResponse.builder()
                .id(id)
                .nomFichier(nomFichier)
                .typeDocument(typeDoc)
                .taille(taille)
                .dateUpload(maintenant)
                .uploadePar(uploader)
                .build();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getNomFichier()).isEqualTo(nomFichier);
        assertThat(response.getTypeDocument()).isEqualTo(typeDoc);
        assertThat(response.getTaille()).isEqualTo(taille);
        assertThat(response.getDateUpload()).isEqualTo(maintenant);
        assertThat(response.getUploadePar().getNomComplet()).isEqualTo("Agent Dupont");
    }

    @Test
    @DisplayName("Vérifie les constructeurs par défaut et complets (Lombok)")
    void shouldVerifyLombokConstructors() {
        DocumentResponse empty = new DocumentResponse();
        assertThat(empty).isNotNull();

        DocumentResponse full = new DocumentResponse(
                1L, "photo.jpg", "IMAGE", 2048L, LocalDateTime.now(), null
        );
        assertThat(full.getNomFichier()).isEqualTo("photo.jpg");
        assertThat(full.getTaille()).isEqualTo(2048L);
    }

    @Test
    @DisplayName("Vérifie que les Getters fonctionnent correctement")
    void shouldVerifyGetters() {
        DocumentResponse response = DocumentResponse.builder()
                .typeDocument("RAPPORT_EXPERT")
                .build();

        assertThat(response.getTypeDocument()).isEqualTo("RAPPORT_EXPERT");
    }
}