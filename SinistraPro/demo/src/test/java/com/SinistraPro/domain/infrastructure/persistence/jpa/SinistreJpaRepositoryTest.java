package com.SinistraPro.domain.infrastructure.persistence.jpa;

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
class SinistreJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SinistreJpaRepository sinistreJpaRepository;


    private UtilisateurEntity persistUtilisateur(String email, Role role) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setNom("User"); u.setPrenom("Test");
        u.setEmail(email); u.setRole(role);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return entityManager.persist(u);
    }

    private SinistreEntity persistSinistre(String numero,
                                           StatutSinistre statut,
                                           UtilisateurEntity client,
                                           UtilisateurEntity agent,
                                           UtilisateurEntity expert) {
        SinistreEntity s = new SinistreEntity();
        s.setNumero(numero);
        s.setTypeSinistre("ACCIDENT");
        s.setStatut(statut);
        s.setDateIncident(LocalDate.now());
        s.setDateDeclaration(LocalDateTime.now());
        s.setClient(client);
        s.setAgent(agent);
        s.setExpert(expert);
        return entityManager.persist(s);
    }


    @Nested
    @DisplayName("findByClientId")
    class FindByClientId {

        @Test
        @DisplayName("Devrait retourner les sinistres d'un client")
        void shouldReturnSinistres_ForClient() {
            UtilisateurEntity client = persistUtilisateur("client1@mail.ma", Role.CLIENT);
            persistSinistre("SIN-C001", StatutSinistre.DECLARE, client, null, null);
            persistSinistre("SIN-C002", StatutSinistre.AFFECTE, client, null, null);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByClientId(client.getId());

            assertThat(result).hasSize(2);
            assertThat(result).extracting(SinistreEntity::getNumero)
                    .containsExactlyInAnyOrder("SIN-C001", "SIN-C002");
        }

        @Test
        @DisplayName("Devrait retourner une liste vide pour un client sans sinistre")
        void shouldReturnEmpty_WhenClientHasNoSinistres() {
            UtilisateurEntity client = persistUtilisateur("client2@mail.ma", Role.CLIENT);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByClientId(client.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner uniquement les sinistres du bon client")
        void shouldReturnOnlySinistres_ForCorrectClient() {
            UtilisateurEntity client1 = persistUtilisateur("client3@mail.ma", Role.CLIENT);
            UtilisateurEntity client2 = persistUtilisateur("client4@mail.ma", Role.CLIENT);
            persistSinistre("SIN-C010", StatutSinistre.DECLARE, client1, null, null);
            persistSinistre("SIN-C011", StatutSinistre.DECLARE, client2, null, null);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByClientId(client1.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNumero()).isEqualTo("SIN-C010");
        }
    }

    @Nested
    @DisplayName("findByExpertId")
    class FindByExpertId {

        @Test
        @DisplayName("Devrait retourner les sinistres assignés à un expert")
        void shouldReturnSinistres_ForExpert() {
            UtilisateurEntity client = persistUtilisateur("client5@mail.ma", Role.CLIENT);
            UtilisateurEntity expert = persistUtilisateur("expert1@mail.ma", Role.EXPERT);
            persistSinistre("SIN-E001", StatutSinistre.EN_EXPERTISE, client, null, expert);
            persistSinistre("SIN-E002", StatutSinistre.EN_EXPERTISE, client, null, expert);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByExpertId(expert.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide pour un expert sans sinistre")
        void shouldReturnEmpty_WhenExpertHasNoSinistres() {
            UtilisateurEntity expert = persistUtilisateur("expert2@mail.ma", Role.EXPERT);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByExpertId(expert.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByAgentId")
    class FindByAgentId {

        @Test
        @DisplayName("Devrait retourner les sinistres assignés à un agent")
        void shouldReturnSinistres_ForAgent() {
            UtilisateurEntity client = persistUtilisateur("client6@mail.ma", Role.CLIENT);
            UtilisateurEntity agent  = persistUtilisateur("agent1@mail.ma",  Role.AGENT);
            persistSinistre("SIN-A001", StatutSinistre.AFFECTE, client, agent, null);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByAgentId(agent.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNumero()).isEqualTo("SIN-A001");
        }

        @Test
        @DisplayName("Devrait retourner une liste vide pour un agent sans sinistre")
        void shouldReturnEmpty_WhenAgentHasNoSinistres() {
            UtilisateurEntity agent = persistUtilisateur("agent2@mail.ma", Role.AGENT);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByAgentId(agent.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByStatut")
    class FindByStatut {

        @Test
        @DisplayName("Devrait retourner les sinistres par statut")
        void shouldReturnSinistres_ByStatut() {
            UtilisateurEntity client = persistUtilisateur("client7@mail.ma", Role.CLIENT);
            persistSinistre("SIN-S001", StatutSinistre.DECLARE,  client, null, null);
            persistSinistre("SIN-S002", StatutSinistre.DECLARE,  client, null, null);
            persistSinistre("SIN-S003", StatutSinistre.AFFECTE,  client, null, null);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByStatut(StatutSinistre.DECLARE);

            assertThat(result).hasSize(2);
            assertThat(result).extracting(SinistreEntity::getStatut)
                    .containsOnly(StatutSinistre.DECLARE);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide si aucun sinistre n'a ce statut")
        void shouldReturnEmpty_WhenNoSinistreWithStatut() {
            UtilisateurEntity client = persistUtilisateur("client8@mail.ma", Role.CLIENT);
            persistSinistre("SIN-S010", StatutSinistre.DECLARE, client, null, null);
            entityManager.flush();

            List<SinistreEntity> result = sinistreJpaRepository.findByStatut(StatutSinistre.CLOTURE);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByNumero")
    class ExistsByNumero {

        @Test
        @DisplayName("Devrait retourner true si le numéro existe")
        void shouldReturnTrue_WhenNumeroExists() {
            UtilisateurEntity client = persistUtilisateur("client9@mail.ma", Role.CLIENT);
            persistSinistre("SIN-UNIQUE-001", StatutSinistre.DECLARE, client, null, null);
            entityManager.flush();

            boolean exists = sinistreJpaRepository.existsByNumero("SIN-UNIQUE-001");

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Devrait retourner false si le numéro n'existe pas")
        void shouldReturnFalse_WhenNumeroDoesNotExist() {
            boolean exists = sinistreJpaRepository.existsByNumero("SIN-INEXISTANT");

            assertThat(exists).isFalse();
        }
    }
}