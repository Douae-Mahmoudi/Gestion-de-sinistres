package com.SinistraPro.domain.infrastructure.persistence.jpa;

import com.SinistraPro.domain.infrastructure.persistence.entity.HistoriqueEntity;
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
class HistoriqueJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HistoriqueJpaRepository historiqueJpaRepository;


    private UtilisateurEntity persistUtilisateur(String email) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setNom("Agent"); u.setPrenom("Test");
        u.setEmail(email); u.setRole(Role.AGENT);
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

    private HistoriqueEntity persistHistorique(StatutSinistre ancienStatut,
                                               StatutSinistre nouveauStatut,
                                               LocalDateTime dateAction,
                                               SinistreEntity sinistre,
                                               UtilisateurEntity effectuePar) {
        HistoriqueEntity h = new HistoriqueEntity();
        h.setAncienStatut(ancienStatut);
        h.setNouveauStatut(nouveauStatut);   // NOT NULL
        h.setDateAction(dateAction);          // NOT NULL
        h.setSinistre(sinistre);              // NOT NULL
        h.setEffectuePar(effectuePar);        // NOT NULL
        return entityManager.persist(h);
    }

    // ─── Tests ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findBySinistreIdOrderByDateActionAsc")
    class FindBySinistreIdOrderByDateActionAsc {

        @Test
        @DisplayName("Devrait retourner les historiques triés par date ascendante")
        void shouldReturnHistoriques_OrderedByDateAsc() {
            UtilisateurEntity agent = persistUtilisateur("agent1@mail.ma");
            UtilisateurEntity client = persistUtilisateur("client1@mail.ma");
            SinistreEntity sinistre = persistSinistre("SIN-H001", client);

            LocalDateTime t1 = LocalDateTime.of(2024, 1, 1, 8, 0);
            LocalDateTime t2 = LocalDateTime.of(2024, 1, 2, 9, 0);
            LocalDateTime t3 = LocalDateTime.of(2024, 1, 3, 10, 0);

            // Persistés dans le désordre intentionnellement
            persistHistorique(StatutSinistre.EVALUE,      StatutSinistre.CLOTURE,    t3, sinistre, agent);
            persistHistorique(null,                        StatutSinistre.DECLARE,    t1, sinistre, agent);
            persistHistorique(StatutSinistre.DECLARE,     StatutSinistre.AFFECTE,    t2, sinistre, agent);
            entityManager.flush();

            List<HistoriqueEntity> result =
                    historiqueJpaRepository.findBySinistreIdOrderByDateActionAsc(sinistre.getId());

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getNouveauStatut()).isEqualTo(StatutSinistre.DECLARE);
            assertThat(result.get(1).getNouveauStatut()).isEqualTo(StatutSinistre.AFFECTE);
            assertThat(result.get(2).getNouveauStatut()).isEqualTo(StatutSinistre.CLOTURE);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide si le sinistre n'a pas d'historique")
        void shouldReturnEmptyList_WhenNoHistorique() {
            UtilisateurEntity client = persistUtilisateur("client2@mail.ma");
            SinistreEntity sinistre = persistSinistre("SIN-H002", client);
            entityManager.flush();

            List<HistoriqueEntity> result =
                    historiqueJpaRepository.findBySinistreIdOrderByDateActionAsc(sinistre.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner une liste vide pour un ID inexistant")
        void shouldReturnEmptyList_WhenSinistreIdDoesNotExist() {
            List<HistoriqueEntity> result =
                    historiqueJpaRepository.findBySinistreIdOrderByDateActionAsc(9999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner uniquement l'historique du sinistre concerné")
        void shouldReturnOnlyHistorique_ForTheCorrectSinistre() {
            UtilisateurEntity agent  = persistUtilisateur("agent3@mail.ma");
            UtilisateurEntity client = persistUtilisateur("client3@mail.ma");
            SinistreEntity sinistre1 = persistSinistre("SIN-H010", client);
            SinistreEntity sinistre2 = persistSinistre("SIN-H011", client);

            persistHistorique(null, StatutSinistre.DECLARE, LocalDateTime.now().minusDays(1), sinistre1, agent);
            persistHistorique(null, StatutSinistre.AFFECTE, LocalDateTime.now(),              sinistre2, agent);
            entityManager.flush();

            List<HistoriqueEntity> result =
                    historiqueJpaRepository.findBySinistreIdOrderByDateActionAsc(sinistre1.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNouveauStatut()).isEqualTo(StatutSinistre.DECLARE);
        }

        @Test
        @DisplayName("Devrait retourner un seul historique correctement")
        void shouldReturnSingleHistorique_WhenOnlyOneExists() {
            UtilisateurEntity agent  = persistUtilisateur("agent4@mail.ma");
            UtilisateurEntity client = persistUtilisateur("client4@mail.ma");
            SinistreEntity sinistre = persistSinistre("SIN-H020", client);
            persistHistorique(null, StatutSinistre.DECLARE, LocalDateTime.now(), sinistre, agent);
            entityManager.flush();

            List<HistoriqueEntity> result =
                    historiqueJpaRepository.findBySinistreIdOrderByDateActionAsc(sinistre.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNouveauStatut()).isEqualTo(StatutSinistre.DECLARE);
        }

        @Test
        @DisplayName("Devrait conserver l'ancien statut quand il est renseigné")
        void shouldPreserveAncienStatut_WhenProvided() {
            UtilisateurEntity agent  = persistUtilisateur("agent5@mail.ma");
            UtilisateurEntity client = persistUtilisateur("client5@mail.ma");
            SinistreEntity sinistre = persistSinistre("SIN-H030", client);
            persistHistorique(StatutSinistre.DECLARE, StatutSinistre.AFFECTE, LocalDateTime.now(), sinistre, agent);
            entityManager.flush();

            List<HistoriqueEntity> result =
                    historiqueJpaRepository.findBySinistreIdOrderByDateActionAsc(sinistre.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAncienStatut()).isEqualTo(StatutSinistre.DECLARE);
            assertThat(result.get(0).getNouveauStatut()).isEqualTo(StatutSinistre.AFFECTE);
        }
    }
}