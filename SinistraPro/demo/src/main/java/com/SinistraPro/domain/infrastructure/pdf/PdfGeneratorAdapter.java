package com.SinistraPro.domain.infrastructure.pdf;

import com.SinistraPro.domain.model.Decision;
import com.SinistraPro.domain.model.Rapport;
import com.SinistraPro.domain.model.Sinistre;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Component
public class PdfGeneratorAdapter {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public byte[] genererDecisionPdf(Sinistre sinistre) {
        Decision decision = sinistre.getDecision();

        if (decision == null) {
            throw new IllegalStateException(
                    "Aucune décision disponible pour ce sinistre");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer   = new PdfWriter(outputStream);
            PdfDocument pdf    = new PdfDocument(writer);
            Document document  = new Document(pdf);

            PdfFont fontBold   = PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont fontNormal = PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            // ── En-tête ──
            document.add(new Paragraph("SinistraPro")
                    .setFont(fontBold).setFontSize(20)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("LETTRE DE DÉCISION")
                    .setFont(fontBold).setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            //Infos dossier
            document.add(new Paragraph("Informations du dossier")
                    .setFont(fontBold).setFontSize(12).setMarginBottom(5));

            Table tableDossier = new Table(
                    UnitValue.createPercentArray(new float[]{40, 60}))
                    .useAllAvailableWidth();

            ajouterLigne(tableDossier, "Numéro de dossier",
                    sinistre.getNumero(), fontBold, fontNormal);
            ajouterLigne(tableDossier, "Type de sinistre",
                    sinistre.getTypeSinistre(), fontBold, fontNormal);
            ajouterLigne(tableDossier, "Date d'incident",
                    sinistre.getDateIncident().format(DATE_FORMAT), fontBold, fontNormal);
            ajouterLigne(tableDossier, "Lieu d'incident",
                    sinistre.getLieuIncident(), fontBold, fontNormal);
            ajouterLigne(tableDossier, "Client",
                    sinistre.getClient().getNomComplet(), fontBold, fontNormal);

            document.add(tableDossier);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Décision")
                    .setFont(fontBold).setFontSize(12).setMarginBottom(5));

            Table tableDecision = new Table(
                    UnitValue.createPercentArray(new float[]{40, 60}))
                    .useAllAvailableWidth();

            ajouterLigne(tableDecision, "Statut",
                    decision.getStatut().name(), fontBold, fontNormal);
            ajouterLigne(tableDecision, "Motif",
                    decision.getMotif(), fontBold, fontNormal);
            ajouterLigne(tableDecision, "Date de décision",
                    decision.getDateDecision().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    fontBold, fontNormal);

            if (decision.getMontantFinal() != null) {
                ajouterLigne(tableDecision, "Montant validé",
                        decision.getMontantFinal() + " MAD", fontBold, fontNormal);
            }

            if (decision.getNumeroVirement() != null) {
                ajouterLigne(tableDecision, "Numéro de virement",
                        decision.getNumeroVirement(), fontBold, fontNormal);
                ajouterLigne(tableDecision, "Date de paiement",
                        decision.getDatePaiement().format(DATE_FORMAT),
                        fontBold, fontNormal);
            }

            document.add(tableDecision);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph(
                    "Superviseur : " + decision.getSuperviseur().getNomComplet())
                    .setFont(fontNormal).setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph(
                    "Document généré le : " +
                            java.time.LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFont(fontNormal).setFontSize(9)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF décision : " + e.getMessage(), e);
        }
    }

    public byte[] genererRapportPdf(Sinistre sinistre, Rapport rapport) {
        if (rapport == null) {
            throw new IllegalStateException(
                    "Aucun rapport disponible pour ce sinistre");
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer   = new PdfWriter(outputStream);
            PdfDocument pdf    = new PdfDocument(writer);
            Document document  = new Document(pdf);

            PdfFont fontBold   = PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont fontNormal = PdfFontFactory.createFont(
                    com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            document.add(new Paragraph("SinistraPro")
                    .setFont(fontBold).setFontSize(20)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("RAPPORT D'EXPERTISE")
                    .setFont(fontBold).setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            document.add(new Paragraph("Informations du dossier")
                    .setFont(fontBold).setFontSize(12).setMarginBottom(5));

            Table tableDossier = new Table(
                    UnitValue.createPercentArray(new float[]{40, 60}))
                    .useAllAvailableWidth();

            ajouterLigne(tableDossier, "Numéro de dossier",
                    sinistre.getNumero(), fontBold, fontNormal);
            ajouterLigne(tableDossier, "Type de sinistre",
                    sinistre.getTypeSinistre(), fontBold, fontNormal);
            ajouterLigne(tableDossier, "Date d'incident",
                    sinistre.getDateIncident().format(DATE_FORMAT), fontBold, fontNormal);
            ajouterLigne(tableDossier, "Lieu d'incident",
                    sinistre.getLieuIncident(), fontBold, fontNormal);
            ajouterLigne(tableDossier, "Client",
                    sinistre.getClient().getNomComplet(), fontBold, fontNormal);

            document.add(tableDossier);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Rapport d'expertise")
                    .setFont(fontBold).setFontSize(12).setMarginBottom(5));

            Table tableRapport = new Table(
                    UnitValue.createPercentArray(new float[]{40, 60}))
                    .useAllAvailableWidth();

            ajouterLigne(tableRapport, "Expert",
                    rapport.getExpert() != null
                            ? rapport.getExpert().getNomComplet() : "—",
                    fontBold, fontNormal);
            ajouterLigne(tableRapport, "Date de soumission",
                    rapport.getDateSoumission().format(DATE_FORMAT),
                    fontBold, fontNormal);
            ajouterLigne(tableRapport, "Montant estimé",
                    rapport.getMontantEstime() + " MAD", fontBold, fontNormal);
            ajouterLigne(tableRapport, "Description des dommages",
                    rapport.getDescriptionDommages(), fontBold, fontNormal);

            if (rapport.getObservations() != null) {
                ajouterLigne(tableRapport, "Observations",
                        rapport.getObservations(), fontBold, fontNormal);
            }

            document.add(tableRapport);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph(
                    "Document généré le : " +
                            java.time.LocalDateTime.now().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFont(fontNormal).setFontSize(9)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erreur génération PDF rapport : " + e.getMessage(), e);
        }
    }


    private void ajouterLigne(Table table, String label, String valeur,
                              PdfFont fontBold, PdfFont fontNormal) {
        table.addCell(new Cell()
                .add(new Paragraph(label).setFont(fontBold).setFontSize(10))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addCell(new Cell()
                .add(new Paragraph(valeur != null ? valeur : "-")
                        .setFont(fontNormal).setFontSize(10)));
    }
}