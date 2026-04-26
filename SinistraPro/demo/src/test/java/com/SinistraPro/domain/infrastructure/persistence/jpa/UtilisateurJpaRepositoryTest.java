package com.SinistraPro.domain.infrastructure.persistence.jpa;

import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UtilisateurJpaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UtilisateurJpaRepository utilisateurJpaRepository;


    private UtilisateurEntity persistUtilisateur(String email, Role role) {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setNom("Dupont"); u.setPrenom("Jean");
        u.setEmail(email); u.setRole(role);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return entityManager.persist(u);
    }

    private UtilisateurEntity persistUtilisateurAvecToken(String email, String token) {
        UtilisateurEntity u = persistUtilisateur(email, Role.CLIENT);
        u.setResetToken(token);
        u.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        return entityManager.persist(u);
    }

    // ─── Tests ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("Devrait retourner l'utilisateur quand l'email existe")
        void shouldReturnUser_WhenEmailExists() {
            persistUtilisateur("jean@mail.ma", Role.CLIENT);
            entityManager.flush();

            Optional<UtilisateurEntity> result = utilisateurJpaRepository.findByEmail("jean@mail.ma");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("jean@mail.ma");
            assertThat(result.get().getRole()).isEqualTo(Role.CLIENT);
        }

        @Test
        @DisplayName("Devrait retourner empty quand l'email n'existe pas")
        void shouldReturnEmpty_WhenEmailDoesNotExist() {
            Optional<UtilisateurEntity> result = utilisateurJpaRepository.findByEmail("inconnu@mail.ma");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByRole")
    class FindByRole {

        @Test
        @DisplayName("Devrait retourner tous les utilisateurs d'un rôle donné")
        void shouldReturnUsers_ByRole() {
            persistUtilisateur("agent1@mail.ma", Role.AGENT);
            persistUtilisateur("agent2@mail.ma", Role.AGENT);
            persistUtilisateur("client1@mail.ma", Role.CLIENT);
            entityManager.flush();

            List<UtilisateurEntity> agents = utilisateurJpaRepository.findByRole(Role.AGENT);

            assertThat(agents).hasSize(2);
            assertThat(agents).extracting(UtilisateurEntity::getRole)
                    .containsOnly(Role.AGENT);
        }

        @Test
        @DisplayName("Devrait retourner une liste vide si aucun utilisateur n'a ce rôle")
        void shouldReturnEmptyList_WhenNoUserWithRole() {
            persistUtilisateur("client2@mail.ma", Role.CLIENT);
            entityManager.flush();

            List<UtilisateurEntity> experts = utilisateurJpaRepository.findByRole(Role.EXPERT);

            assertThat(experts).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("Devrait retourner true quand l'email existe")
        void shouldReturnTrue_WhenEmailExists() {
            persistUtilisateur("exists@mail.ma", Role.CLIENT);
            entityManager.flush();

            boolean exists = utilisateurJpaRepository.existsByEmail("exists@mail.ma");

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("Devrait retourner false quand l'email n'existe pas")
        void shouldReturnFalse_WhenEmailDoesNotExist() {
            boolean exists = utilisateurJpaRepository.existsByEmail("ghost@mail.ma");

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByResetToken")
    class FindByResetToken {

        @Test
        @DisplayName("Devrait retourner l'utilisateur quand le token existe")
        void shouldReturnUser_WhenTokenExists() {
            persistUtilisateurAvecToken("reset1@mail.ma", "TOKEN-ABC-123");
            entityManager.flush();

            Optional<UtilisateurEntity> result = utilisateurJpaRepository.findByResetToken("TOKEN-ABC-123");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("reset1@mail.ma");
        }

        @Test
        @DisplayName("Devrait retourner empty quand le token n'existe pas")
        void shouldReturnEmpty_WhenTokenDoesNotExist() {
            Optional<UtilisateurEntity> result = utilisateurJpaRepository.findByResetToken("TOKEN-INCONNU");

            assertThat(result).isEmpty();
        }
    }
}