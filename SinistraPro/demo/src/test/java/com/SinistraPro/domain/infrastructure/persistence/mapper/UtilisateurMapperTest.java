package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UtilisateurMapper")
class UtilisateurMapperTest {

    private UtilisateurMapper utilisateurMapper;

    private static final Long          ID                 = 1L;
    private static final String        NOM                = "Benali";
    private static final String        PRENOM             = "Youssef";
    private static final String        EMAIL              = "youssef.benali@sinistrapro.ma";
    private static final String        MOT_DE_PASSE       = "$2a$10$hashedPassword";
    private static final String        TELEPHONE          = "+212600000001";
    private static final Role          ROLE               = Role.CLIENT;
    private static final LocalDateTime DATE_CREATION      = LocalDateTime.of(2024, 1, 10, 9, 0);
    private static final String        RESET_TOKEN        = "abc123token";
    private static final LocalDateTime RESET_TOKEN_EXPIRY = LocalDateTime.of(2024, 1, 10, 10, 0);

    private UtilisateurEntity entity;
    private Utilisateur       domain;

    @BeforeEach
    void setUp() {
        utilisateurMapper = new UtilisateurMapper();

        entity = UtilisateurEntity.builder()
                .id(ID)
                .nom(NOM)
                .prenom(PRENOM)
                .email(EMAIL)
                .motDePasse(MOT_DE_PASSE)
                .telephone(TELEPHONE)
                .role(ROLE)
                .dateCreation(DATE_CREATION)
                .resetToken(RESET_TOKEN)
                .resetTokenExpiry(RESET_TOKEN_EXPIRY)
                .build();

        domain = Utilisateur.builder()
                .id(ID)
                .nom(NOM)
                .prenom(PRENOM)
                .email(EMAIL)
                .motDePasse(MOT_DE_PASSE)
                .telephone(TELEPHONE)
                .role(ROLE)
                .dateCreation(DATE_CREATION)
                .resetToken(RESET_TOKEN)
                .resetTokenExpiry(RESET_TOKEN_EXPIRY)
                .build();
    }

    @Nested
    @DisplayName("toDomain(UtilisateurEntity)")
    class ToDomainTests {

        @Test
        @DisplayName("retourne null si l'entité est null")
        void shouldReturnNullWhenEntityIsNull() {
            assertThat(utilisateurMapper.toDomain(null)).isNull();
        }

        @Test
        @DisplayName("mappe tous les champs de base correctement")
        void shouldMapAllBaseFields() {
            Utilisateur result = utilisateurMapper.toDomain(entity);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getNom()).isEqualTo(NOM);
            assertThat(result.getPrenom()).isEqualTo(PRENOM);
            assertThat(result.getEmail()).isEqualTo(EMAIL);
            assertThat(result.getMotDePasse()).isEqualTo(MOT_DE_PASSE);
            assertThat(result.getTelephone()).isEqualTo(TELEPHONE);
            assertThat(result.getRole()).isEqualTo(ROLE);
            assertThat(result.getDateCreation()).isEqualTo(DATE_CREATION);
        }

        @Test
        @DisplayName("mappe resetToken et resetTokenExpiry")
        void shouldMapResetTokenFields() {
            Utilisateur result = utilisateurMapper.toDomain(entity);

            assertThat(result.getResetToken()).isEqualTo(RESET_TOKEN);
            assertThat(result.getResetTokenExpiry()).isEqualTo(RESET_TOKEN_EXPIRY);
        }

        @Test
        @DisplayName("mappe resetToken null sans erreur")
        void shouldHandleNullResetToken() {
            entity = UtilisateurEntity.builder()
                    .id(ID)
                    .nom(NOM)
                    .email(EMAIL)
                    .role(ROLE)
                    .resetToken(null)
                    .resetTokenExpiry(null)
                    .build();

            Utilisateur result = utilisateurMapper.toDomain(entity);

            assertThat(result.getResetToken()).isNull();
            assertThat(result.getResetTokenExpiry()).isNull();
        }

        @Test
        @DisplayName("mappe correctement chaque valeur du Role enum")
        void shouldMapEachRoleValue() {
            for (Role role : Role.values()) {
                UtilisateurEntity e = UtilisateurEntity.builder().id(1L).role(role).build();
                Utilisateur result = utilisateurMapper.toDomain(e);
                assertThat(result.getRole()).isEqualTo(role);
            }
        }

        @Test
        @DisplayName("mappe une entité avec uniquement l'id (champs optionnels null)")
        void shouldMapEntityWithOnlyId() {
            UtilisateurEntity minimal = UtilisateurEntity.builder().id(99L).build();

            Utilisateur result = utilisateurMapper.toDomain(minimal);

            assertThat(result.getId()).isEqualTo(99L);
            assertThat(result.getNom()).isNull();
            assertThat(result.getEmail()).isNull();
            assertThat(result.getRole()).isNull();
            assertThat(result.getResetToken()).isNull();
            assertThat(result.getResetTokenExpiry()).isNull();
        }
    }

    @Nested
    @DisplayName("toEntity(Utilisateur)")
    class ToEntityTests {

        @Test
        @DisplayName("retourne null si le domaine est null")
        void shouldReturnNullWhenDomainIsNull() {
            assertThat(utilisateurMapper.toEntity(null)).isNull();
        }

        @Test
        @DisplayName("mappe tous les champs de base correctement")
        void shouldMapAllBaseFields() {
            UtilisateurEntity result = utilisateurMapper.toEntity(domain);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getNom()).isEqualTo(NOM);
            assertThat(result.getPrenom()).isEqualTo(PRENOM);
            assertThat(result.getEmail()).isEqualTo(EMAIL);
            assertThat(result.getMotDePasse()).isEqualTo(MOT_DE_PASSE);
            assertThat(result.getTelephone()).isEqualTo(TELEPHONE);
            assertThat(result.getRole()).isEqualTo(ROLE);
            assertThat(result.getDateCreation()).isEqualTo(DATE_CREATION);
        }

        @Test
        @DisplayName("mappe resetToken et resetTokenExpiry")
        void shouldMapResetTokenFields() {
            UtilisateurEntity result = utilisateurMapper.toEntity(domain);

            assertThat(result.getResetToken()).isEqualTo(RESET_TOKEN);
            assertThat(result.getResetTokenExpiry()).isEqualTo(RESET_TOKEN_EXPIRY);
        }

        @Test
        @DisplayName("mappe resetToken null sans erreur")
        void shouldHandleNullResetToken() {
            domain = Utilisateur.builder()
                    .id(ID)
                    .nom(NOM)
                    .email(EMAIL)
                    .role(ROLE)
                    .resetToken(null)
                    .resetTokenExpiry(null)
                    .build();

            UtilisateurEntity result = utilisateurMapper.toEntity(domain);

            assertThat(result.getResetToken()).isNull();
            assertThat(result.getResetTokenExpiry()).isNull();
        }

        @Test
        @DisplayName("mappe un domaine avec uniquement l'id (champs optionnels null)")
        void shouldMapDomainWithOnlyId() {
            Utilisateur minimal = Utilisateur.builder().id(99L).build();

            UtilisateurEntity result = utilisateurMapper.toEntity(minimal);

            assertThat(result.getId()).isEqualTo(99L);
            assertThat(result.getNom()).isNull();
            assertThat(result.getEmail()).isNull();
            assertThat(result.getRole()).isNull();
            assertThat(result.getResetToken()).isNull();
            assertThat(result.getResetTokenExpiry()).isNull();
        }
    }

    @Nested
    @DisplayName("Round-trip")
    class RoundTripTests {

        @Test
        @DisplayName("entity domain entity préserve tous les champs")
        void entityToDomainToEntityPreservesAllFields() {
            Utilisateur d = utilisateurMapper.toDomain(entity);
            UtilisateurEntity reconverted = utilisateurMapper.toEntity(d);

            assertThat(reconverted.getId()).isEqualTo(entity.getId());
            assertThat(reconverted.getNom()).isEqualTo(entity.getNom());
            assertThat(reconverted.getPrenom()).isEqualTo(entity.getPrenom());
            assertThat(reconverted.getEmail()).isEqualTo(entity.getEmail());
            assertThat(reconverted.getMotDePasse()).isEqualTo(entity.getMotDePasse());
            assertThat(reconverted.getTelephone()).isEqualTo(entity.getTelephone());
            assertThat(reconverted.getRole()).isEqualTo(entity.getRole());
            assertThat(reconverted.getDateCreation()).isEqualTo(entity.getDateCreation());
            assertThat(reconverted.getResetToken()).isEqualTo(entity.getResetToken());
            assertThat(reconverted.getResetTokenExpiry()).isEqualTo(entity.getResetTokenExpiry());
        }

        @Test
        @DisplayName("domain entity domain préserve tous les champs")
        void domainToEntityToDomainPreservesAllFields() {
            UtilisateurEntity e = utilisateurMapper.toEntity(domain);
            Utilisateur reconverted = utilisateurMapper.toDomain(e);

            assertThat(reconverted.getId()).isEqualTo(domain.getId());
            assertThat(reconverted.getNom()).isEqualTo(domain.getNom());
            assertThat(reconverted.getEmail()).isEqualTo(domain.getEmail());
            assertThat(reconverted.getResetToken()).isEqualTo(domain.getResetToken());
            assertThat(reconverted.getResetTokenExpiry()).isEqualTo(domain.getResetTokenExpiry());
        }
    }
}