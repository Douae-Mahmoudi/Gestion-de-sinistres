package com.SinistraPro.domain.infrastructure.persistence.jpa;

import com.SinistraPro.domain.infrastructure.persistence.entity.DocumentEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.StatutSinistre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DocumentJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentJpaRepository documentJpaRepository;


    private UtilisateurEntity persistUtilisateur(String email, Role role) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setNom("User"); u.setPrenom("Test");
        u.setEmail(email); u.setRole(role);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return entityManager.persist(u);
    }

    private SinistreEntity persistSinistre(String numero, UtilisateurEntity client) {
        SinistreEntity s = new SinistreEntity();
        s.setNumero(numero);
        s.setTypeSinistre("ACCIDENT");
        s.setStatut(StatutSinistre.DECLARE);
        s.setDateIncident(LocalDate.now());
        s.setDateDeclaration(LocalDateTime.now());
        s.setClient(client);
        return entityManager.persist(s);
    }

    private DocumentEntity persistDocument(String nom, String type,
                                           SinistreEntity sinistre,
                                           UtilisateurEntity uploadeur) {
        DocumentEntity d = new DocumentEntity();
        d.setNomFichier(nom);
        d.setTypeDocument(type);
        d.setCheminFichier("/uploads/" + nom);
        d.setDateUpload(LocalDateTime.now());
        d.setSinistre(sinistre);
        d.setUploadePar(uploadeur);
        return entityManager.persist(d);
    }


    @Nested
    @DisplayName("findBySinistreId")
    class FindBySinistreId {

        @Test
        @DisplayName("Devrait retourner les documents associés à un sinistre")
        void shouldReturnDocuments_WhenSinistreHasDocuments() {
            UtilisateurEntity client = persistUtilisateur("client1@mail.ma", Role.CLIENT);
            SinistreEntity sinistre = persistSinistre("SIN-001", client);
            persistDocument("facture.pdf", "FACTURE", sinistre, client);
            persistDocument("constat.pdf", "CONSTAT", sinistre, client);
            entityManager.flush();

            List<DocumentEntity> result = documentJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).hasSize(2);
            assertThat(result).extracting(DocumentEntity::getNomFichier)
                    .containsExactlyInAnyOrder("facture.pdf", "constat.pdf");
        }

        @Test
        @DisplayName("Devrait retourner une liste vide si le sinistre n'a pas de documents")
        void shouldReturnEmptyList_WhenSinistreHasNoDocuments() {
            UtilisateurEntity client = persistUtilisateur("client2@mail.ma", Role.CLIENT);
            SinistreEntity sinistre = persistSinistre("SIN-002", client);
            entityManager.flush();

            List<DocumentEntity> result = documentJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner une liste vide pour un ID de sinistre inexistant")
        void shouldReturnEmptyList_WhenSinistreIdDoesNotExist() {
            List<DocumentEntity> result = documentJpaRepository.findBySinistreId(9999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner uniquement les documents du sinistre concerné")
        void shouldReturnOnlyDocuments_ForTheCorrectSinistre() {
            UtilisateurEntity client = persistUtilisateur("client3@mail.ma", Role.CLIENT);
            SinistreEntity sinistre1 = persistSinistre("SIN-010", client);
            SinistreEntity sinistre2 = persistSinistre("SIN-011", client);
            persistDocument("doc-s1.pdf", "FACTURE", sinistre1, client);
            persistDocument("doc-s2.pdf", "CONSTAT", sinistre2, client);
            entityManager.flush();

            List<DocumentEntity> result = documentJpaRepository.findBySinistreId(sinistre1.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNomFichier()).isEqualTo("doc-s1.pdf");
        }

        @Test
        @DisplayName("Devrait retourner le bon type de document")
        void shouldReturnCorrectDocumentType() {
            UtilisateurEntity client = persistUtilisateur("client4@mail.ma", Role.CLIENT);
            SinistreEntity sinistre = persistSinistre("SIN-020", client);
            persistDocument("rapport.pdf", "RAPPORT_EXPERT", sinistre, client);
            entityManager.flush();

            List<DocumentEntity> result = documentJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTypeDocument()).isEqualTo("RAPPORT_EXPERT");
        }
    }
}