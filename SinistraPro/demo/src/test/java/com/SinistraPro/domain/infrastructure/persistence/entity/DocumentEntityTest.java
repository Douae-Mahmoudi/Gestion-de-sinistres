package com.SinistraPro.domain.infrastructure.persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentEntityTest {

    @Test
    @DisplayName("Vérification du Builder et de l'intégrité des données DocumentEntity")
    void testDocumentEntityBuilder() {
        LocalDateTime now = LocalDateTime.now();
        UtilisateurEntity client = UtilisateurEntity.builder().id(1L).nom("Alami").build();
        SinistreEntity sinistre = SinistreEntity.builder().id(50L).build();

        DocumentEntity document = DocumentEntity.builder()
                .id(10L)
                .nomFichier("constat_amiable.pdf")
                .cheminFichier("/uploads/sinistres/50/constat.pdf")
                .typeDocument("CONSTAT")
                .taille(102400L)
                .dateUpload(now)
                .uploadePar(client)
                .sinistre(sinistre)
                .build();

        assertThat(document.getId()).isEqualTo(10L);
        assertThat(document.getNomFichier()).isEqualTo("constat_amiable.pdf");
        assertThat(document.getTaille()).isEqualTo(102400L);
        assertThat(document.getUploadePar().getNom()).isEqualTo("Alami");
        assertThat(document.getSinistre().getId()).isEqualTo(50L);
        assertThat(document.getDateUpload()).isEqualTo(now);
    }
}