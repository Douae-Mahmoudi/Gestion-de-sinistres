package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentTest {

    @Test
    @DisplayName("Devrait créer un Document complet avec le Builder")
    void shouldCreateDocumentWithBuilder() {
        LocalDateTime now = LocalDateTime.now();
        Utilisateur mockUser = new Utilisateur();
        String nomFichier = "constat_amiable.pdf";
        String chemin = "/storage/documents/2024/01/";

        Document document = Document.builder()
                .id(500L)
                .nomFichier(nomFichier)
                .cheminFichier(chemin + nomFichier)
                .typeDocument("CONSTAT")
                .taille(2048L)
                .dateUpload(now)
                .uploadePar(mockUser)
                .build();

        assertThat(document).isNotNull();
        assertThat(document.getId()).isEqualTo(500L);
        assertThat(document.getNomFichier()).isEqualTo(nomFichier);
        assertThat(document.getCheminFichier()).contains("constat_amiable.pdf");
        assertThat(document.getTypeDocument()).isEqualTo("CONSTAT");
        assertThat(document.getTaille()).isEqualTo(2048L);
        assertThat(document.getDateUpload()).isEqualTo(now);
        assertThat(document.getUploadePar()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("Devrait valider les Getters et Setters")
    void shouldTestGettersAndSetters() {
        Document document = new Document();
        String type = "PHOTO";

        document.setTypeDocument(type);
        document.setTaille(5000L);

        assertThat(document.getTypeDocument()).isEqualTo(type);
        assertThat(document.getTaille()).isEqualTo(5000L);
    }

    @Test
    @DisplayName("Devrait vérifier l'existence des constructeurs (Lombok)")
    void shouldVerifyConstructors() {
        Utilisateur user = new Utilisateur();
        LocalDateTime time = LocalDateTime.now();
        Document docFull = new Document(1L, "img.png", "/path", "PHOTO", 100L, time, user);

        assertThat(docFull.getId()).isEqualTo(1L);

        Document docEmpty = new Document();
        assertThat(docEmpty).isNotNull();
        assertThat(docEmpty.getId()).isNull();
    }
}