package com.SinistraPro.domain.infrastructure.persistence.jpa;

import com.SinistraPro.domain.infrastructure.persistence.entity.DecisionEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.StatutDecision;
import com.SinistraPro.domain.model.StatutSinistre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DecisionJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DecisionJpaRepository decisionJpaRepository;


    private UtilisateurEntity createAndPersistSuperviseur(String email) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setNom("Benani");
        u.setPrenom("Sami");
        u.setEmail(email);
        u.setRole(Role.SUPERVISEUR);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return entityManager.persist(u);
    }

    private UtilisateurEntity createAndPersistClient(String email) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setNom("Client");
        u.setPrenom("X");
        u.setEmail(email);
        u.setRole(Role.CLIENT);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return entityManager.persist(u);
    }

    private DecisionEntity createAndPersistDecision(StatutDecision statut,
                                                    UtilisateurEntity superviseur) {
        DecisionEntity d = new DecisionEntity();
        d.setStatut(statut);
        d.setDateDecision(LocalDateTime.now());
        d.setSuperviseur(superviseur);
        return entityManager.persist(d);
    }

    private SinistreEntity createAndPersistSinistre(String numero,
                                                    UtilisateurEntity client,
                                                    DecisionEntity decision) {
        SinistreEntity s = new SinistreEntity();
        s.setNumero(numero);
        s.setTypeSinistre("ACCIDENT");
        s.setStatut(StatutSinistre.DECLARE);
        s.setDateIncident(LocalDate.now());
        s.setDateDeclaration(LocalDateTime.now());
        s.setClient(client);
        s.setDecision(decision);
        return entityManager.persist(s);
    }


    @Nested
    @DisplayName("findBySinistreId")
    class FindBySinistreId {

        @Test
        @DisplayName("Devrait retourner la décision quand le sinistre possède une décision")
        void shouldReturnDecision_WhenSinistreHasDecision() {
            UtilisateurEntity superviseur = createAndPersistSuperviseur("sami@pro.ma");
            DecisionEntity decision = createAndPersistDecision(StatutDecision.APPROUVE, superviseur);
            UtilisateurEntity client = createAndPersistClient("client@mail.ma");
            SinistreEntity sinistre = createAndPersistSinistre("SIN-001", client, decision);
            entityManager.flush();

            Optional<DecisionEntity> result = decisionJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(decision.getId());
            assertThat(result.get().getStatut()).isEqualTo(StatutDecision.APPROUVE);
        }

        @Test
        @DisplayName("Devrait retourner empty quand aucun sinistre ne correspond à l'ID")
        void shouldReturnEmpty_WhenSinistreIdDoesNotExist() {
            Optional<DecisionEntity> result = decisionJpaRepository.findBySinistreId(9999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner empty quand le sinistre n'a pas de décision")
        void shouldReturnEmpty_WhenSinistreHasNoDecision() {
            UtilisateurEntity client = createAndPersistClient("noclient@mail.ma");
            SinistreEntity sinistre = createAndPersistSinistre("SIN-002", client, null);
            entityManager.flush();

            Optional<DecisionEntity> result = decisionJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner la bonne décision parmi plusieurs sinistres")
        void shouldReturnCorrectDecision_WhenMultipleSinistresExist() {
            UtilisateurEntity superviseur = createAndPersistSuperviseur("sup@pro.ma");
            UtilisateurEntity client = createAndPersistClient("multi@mail.ma");

            DecisionEntity decision1 = createAndPersistDecision(StatutDecision.APPROUVE, superviseur);
            DecisionEntity decision2 = createAndPersistDecision(StatutDecision.REJETE, superviseur);

            SinistreEntity sinistre1 = createAndPersistSinistre("SIN-010", client, decision1);
            SinistreEntity sinistre2 = createAndPersistSinistre("SIN-011", client, decision2);
            entityManager.flush();

            Optional<DecisionEntity> result1 = decisionJpaRepository.findBySinistreId(sinistre1.getId());
            Optional<DecisionEntity> result2 = decisionJpaRepository.findBySinistreId(sinistre2.getId());

            assertThat(result1).isPresent();
            assertThat(result1.get().getStatut()).isEqualTo(StatutDecision.APPROUVE);

            assertThat(result2).isPresent();
            assertThat(result2.get().getStatut()).isEqualTo(StatutDecision.REJETE);
        }

        @Test
        @DisplayName("Devrait retourner la décision avec les données superviseur correctes")
        void shouldReturnDecision_WithCorrectSuperviseurData() {
            UtilisateurEntity superviseur = createAndPersistSuperviseur("verified@pro.ma");
            DecisionEntity decision = createAndPersistDecision(StatutDecision.APPROUVE, superviseur);
            UtilisateurEntity client = createAndPersistClient("sup-client@mail.ma");
            SinistreEntity sinistre = createAndPersistSinistre("SIN-020", client, decision);
            entityManager.flush();

            Optional<DecisionEntity> result = decisionJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getSuperviseur()).isNotNull();
            assertThat(result.get().getSuperviseur().getEmail()).isEqualTo("verified@pro.ma");
            assertThat(result.get().getSuperviseur().getRole()).isEqualTo(Role.SUPERVISEUR);
        }

        @Test
        @DisplayName("Devrait retourner la décision avec statut REJETE correctement")
        void shouldReturnDecision_WhenStatutIsRejete() {
            UtilisateurEntity superviseur = createAndPersistSuperviseur("rej@pro.ma");
            DecisionEntity decision = createAndPersistDecision(StatutDecision.REJETE, superviseur);
            UtilisateurEntity client = createAndPersistClient("rej-client@mail.ma");
            SinistreEntity sinistre = createAndPersistSinistre("SIN-030", client, decision);
            entityManager.flush();

            Optional<DecisionEntity> result = decisionJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getStatut()).isEqualTo(StatutDecision.REJETE);
        }
    }
}