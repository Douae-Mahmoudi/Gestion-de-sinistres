package com.SinistraPro.domain.infrastructure.pdf;

import com.SinistraPro.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PdfGeneratorAdapterTest {

    private PdfGeneratorAdapter pdfGeneratorAdapter;
    private Sinistre sinistre;

    @BeforeEach
    void setUp() {
        pdfGeneratorAdapter = new PdfGeneratorAdapter();

        Utilisateur client = Utilisateur.builder()
                .nom("Alami")
                .prenom("Karim")
                .build();

        Utilisateur superviseur = Utilisateur.builder()
                .nom("Benani")
                .build();

        Decision decision = Decision.builder()
                .statut(StatutDecision.APPROUVE)
                .motif("Dossier complet et validé")
                .montantFinal(new BigDecimal("15500.50"))
                .dateDecision(LocalDateTime.now())
                .superviseur(superviseur)
                .numeroVirement("VIR-998877")
                .datePaiement(LocalDate.now().plusDays(2))
                .build();

        sinistre = Sinistre.builder()
                .numero("SIN-2026-001")
                .typeSinistre("ACCIDENT_AUTO")
                .dateIncident(LocalDate.now().minusDays(5))
                .lieuIncident("Rabat")
                .client(client)
                .decision(decision)
                .build();
    }

    @Test
    @DisplayName("Succès : Devrait générer un tableau d'octets PDF non vide")
    void genererDecisionPdf_ShouldReturnByteArray() {
        byte[] pdfContent = pdfGeneratorAdapter.genererDecisionPdf(sinistre);

        assertThat(pdfContent).isNotNull();
        assertThat(pdfContent.length).isGreaterThan(0);

        String header = new String(pdfContent, 0, 4);
        assertThat(header).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("Échec : Devrait lever une exception si la décision est absente")
    void genererDecisionPdf_ShouldFailWhenNoDecision() {
        sinistre.setDecision(null);

        assertThatThrownBy(() -> pdfGeneratorAdapter.genererDecisionPdf(sinistre))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Aucune décision disponible");
    }

    @Test
    @DisplayName("Succès : Devrait gérer les champs optionnels nuls (Virement)")
    void genererDecisionPdf_ShouldHandleMissingPaymentInfo() {
        sinistre.getDecision().setNumeroVirement(null);
        sinistre.getDecision().setDatePaiement(null);

        byte[] pdfContent = pdfGeneratorAdapter.genererDecisionPdf(sinistre);

        assertThat(pdfContent).isNotEmpty();
    }
}