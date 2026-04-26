package com.SinistraPro.domain.infrastructure.persistence.jpa;

import com.SinistraPro.domain.infrastructure.persistence.entity.RapportEntity;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RapportJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RapportJpaRepository rapportJpaRepository;


    private UtilisateurEntity persistUtilisateur(String email, Role role) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setNom("User"); u.setPrenom("Test");
        u.setEmail(email); u.setRole(role);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return entityManager.persist(u);
    }

    private RapportEntity persistRapport(BigDecimal montant, UtilisateurEntity expert) {
        RapportEntity r = new RapportEntity();
        r.setMontantEstime(montant);
        r.setDateSoumission(LocalDateTime.now());
        r.setExpert(expert);
        r.setDescriptionDommages("Dommages constatés");
        r.setObservations("Aucune observation");
        return entityManager.persist(r);
    }

    private SinistreEntity persistSinistreAvecRapport(String numero,
                                                      UtilisateurEntity client,
                                                      RapportEntity rapport) {
        SinistreEntity s = new SinistreEntity();
        s.setNumero(numero);
        s.setTypeSinistre("ACCIDENT");
        s.setStatut(StatutSinistre.EVALUE);
        s.setDateIncident(LocalDate.now());
        s.setDateDeclaration(LocalDateTime.now());
        s.setClient(client);
        s.setRapport(rapport);
        return entityManager.persist(s);
    }

    private SinistreEntity persistSinistreSansRapport(String numero, UtilisateurEntity client) {
        SinistreEntity s = new SinistreEntity();
        s.setNumero(numero);
        s.setTypeSinistre("ACCIDENT");
        s.setStatut(StatutSinistre.DECLARE);
        s.setDateIncident(LocalDate.now());
        s.setDateDeclaration(LocalDateTime.now());
        s.setClient(client);
        return entityManager.persist(s);
    }


    @Nested
    @DisplayName("findBySinistreId")
    class FindBySinistreId {

        @Test
        @DisplayName("Devrait retourner le rapport quand le sinistre en possède un")
        void shouldReturnRapport_WhenSinistreHasRapport() {
            UtilisateurEntity expert = persistUtilisateur("expert1@mail.ma", Role.EXPERT);
            UtilisateurEntity client = persistUtilisateur("client1@mail.ma", Role.CLIENT);
            RapportEntity rapport = persistRapport(new BigDecimal("5000.00"), expert);
            SinistreEntity sinistre = persistSinistreAvecRapport("SIN-R001", client, rapport);
            entityManager.flush();

            Optional<RapportEntity> result = rapportJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(rapport.getId());
            assertThat(result.get().getMontantEstime()).isEqualByComparingTo(new BigDecimal("5000.00"));
        }

        @Test
        @DisplayName("Devrait retourner empty quand le sinistre n'a pas de rapport")
        void shouldReturnEmpty_WhenSinistreHasNoRapport() {
            UtilisateurEntity client = persistUtilisateur("client2@mail.ma", Role.CLIENT);
            SinistreEntity sinistre = persistSinistreSansRapport("SIN-R002", client);
            entityManager.flush();

            Optional<RapportEntity> result = rapportJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner empty pour un ID de sinistre inexistant")
        void shouldReturnEmpty_WhenSinistreIdDoesNotExist() {
            Optional<RapportEntity> result = rapportJpaRepository.findBySinistreId(9999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Devrait retourner le bon rapport parmi plusieurs sinistres")
        void shouldReturnCorrectRapport_WhenMultipleSinistresExist() {
            UtilisateurEntity expert = persistUtilisateur("expert4@mail.ma", Role.EXPERT);
            UtilisateurEntity client = persistUtilisateur("client4@mail.ma", Role.CLIENT);

            RapportEntity rapport1 = persistRapport(new BigDecimal("1000.00"), expert);
            RapportEntity rapport2 = persistRapport(new BigDecimal("9000.00"), expert);

            SinistreEntity sinistre1 = persistSinistreAvecRapport("SIN-R010", client, rapport1);
            SinistreEntity sinistre2 = persistSinistreAvecRapport("SIN-R011", client, rapport2);
            entityManager.flush();

            Optional<RapportEntity> result1 = rapportJpaRepository.findBySinistreId(sinistre1.getId());
            Optional<RapportEntity> result2 = rapportJpaRepository.findBySinistreId(sinistre2.getId());

            assertThat(result1).isPresent();
            assertThat(result1.get().getMontantEstime()).isEqualByComparingTo(new BigDecimal("1000.00"));

            assertThat(result2).isPresent();
            assertThat(result2.get().getMontantEstime()).isEqualByComparingTo(new BigDecimal("9000.00"));
        }

        @Test
        @DisplayName("Devrait retourner les données complètes du rapport")
        void shouldReturnFullRapportData() {
            UtilisateurEntity expert = persistUtilisateur("expert5@mail.ma", Role.EXPERT);
            UtilisateurEntity client = persistUtilisateur("client5@mail.ma", Role.CLIENT);
            RapportEntity rapport = persistRapport(new BigDecimal("3500.50"), expert);
            rapport.setDescriptionDommages("Choc frontal important");
            rapport.setObservations("Véhicule irréparable");
            SinistreEntity sinistre = persistSinistreAvecRapport("SIN-R020", client, rapport);
            entityManager.flush();

            Optional<RapportEntity> result = rapportJpaRepository.findBySinistreId(sinistre.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getDescriptionDommages()).isEqualTo("Choc frontal important");
            assertThat(result.get().getObservations()).isEqualTo("Véhicule irréparable");
            assertThat(result.get().getExpert().getEmail()).isEqualTo("expert5@mail.ma");
        }
    }
}