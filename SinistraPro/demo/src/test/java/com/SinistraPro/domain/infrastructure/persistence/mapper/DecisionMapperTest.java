package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.infrastructure.persistence.entity.DecisionEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Decision;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.StatutDecision;
import com.SinistraPro.domain.model.Utilisateur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionMapperTest {

    @Mock
    private UtilisateurMapper utilisateurMapper;

    @InjectMocks
    private DecisionMapper decisionMapper;


    private UtilisateurEntity buildSuperviseurEntity() {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setId(10L);
        u.setNom("Benani"); u.setPrenom("Sami");
        u.setEmail("sami@pro.ma");
        u.setRole(Role.SUPERVISEUR);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return u;
    }

    private Utilisateur buildSuperviseurDomain() {
        return Utilisateur.builder()
                .id(10L)
                .nom("Benani").prenom("Sami")
                .email("sami@pro.ma")
                .role(Role.SUPERVISEUR)
                .build();
    }

    private DecisionEntity buildDecisionEntity(UtilisateurEntity superviseur) {
        DecisionEntity e = new DecisionEntity();
        e.setId(1L);
        e.setStatut(StatutDecision.APPROUVE);
        e.setMontantFinal(new BigDecimal("8000.00"));
        e.setMotif("Dommages validés");
        e.setDateDecision(LocalDateTime.of(2024, 6, 1, 10, 0));
        e.setNumeroVirement("VIR-001");
        e.setDatePaiement(LocalDate.of(2024, 6, 5));
        e.setSuperviseur(superviseur);
        return e;
    }

    private Decision buildDecisionDomain(Utilisateur superviseur) {
        return Decision.builder()
                .id(1L)
                .statut(StatutDecision.APPROUVE)
                .montantFinal(new BigDecimal("8000.00"))
                .motif("Dommages validés")
                .dateDecision(LocalDateTime.of(2024, 6, 1, 10, 0))
                .numeroVirement("VIR-001")
                .datePaiement(LocalDate.of(2024, 6, 5))
                .superviseur(superviseur)
                .build();
    }


    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("Devrait mapper tous les champs de l'entity vers le domain")
        void shouldMapAllFields_FromEntityToDomain() {
            UtilisateurEntity supEntity = buildSuperviseurEntity();
            Utilisateur supDomain = buildSuperviseurDomain();
            DecisionEntity entity = buildDecisionEntity(supEntity);

            when(utilisateurMapper.toDomain(supEntity)).thenReturn(supDomain);

            Decision result = decisionMapper.toDomain(entity);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStatut()).isEqualTo(StatutDecision.APPROUVE);
            assertThat(result.getMontantFinal()).isEqualByComparingTo(new BigDecimal("8000.00"));
            assertThat(result.getMotif()).isEqualTo("Dommages validés");
            assertThat(result.getNumeroVirement()).isEqualTo("VIR-001");
            assertThat(result.getDatePaiement()).isEqualTo(LocalDate.of(2024, 6, 5));
            assertThat(result.getSuperviseur()).isEqualTo(supDomain);

            verify(utilisateurMapper).toDomain(supEntity);
        }

        @Test
        @DisplayName("Devrait retourner null si l'entity est null")
        void shouldReturnNull_WhenEntityIsNull() {
            Decision result = decisionMapper.toDomain(null);

            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper);
        }

        @Test
        @DisplayName("Devrait mapper statut REJETE correctement")
        void shouldMapStatutRejete() {
            UtilisateurEntity supEntity = buildSuperviseurEntity();
            DecisionEntity entity = buildDecisionEntity(supEntity);
            entity.setStatut(StatutDecision.REJETE);

            when(utilisateurMapper.toDomain(supEntity)).thenReturn(buildSuperviseurDomain());

            Decision result = decisionMapper.toDomain(entity);

            assertThat(result.getStatut()).isEqualTo(StatutDecision.REJETE);
        }
    }


    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("Devrait mapper tous les champs du domain vers l'entity")
        void shouldMapAllFields_FromDomainToEntity() {
            Utilisateur supDomain = buildSuperviseurDomain();
            UtilisateurEntity supEntity = buildSuperviseurEntity();
            Decision domain = buildDecisionDomain(supDomain);

            when(utilisateurMapper.toEntity(supDomain)).thenReturn(supEntity);

            DecisionEntity result = decisionMapper.toEntity(domain);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getStatut()).isEqualTo(StatutDecision.APPROUVE);
            assertThat(result.getMontantFinal()).isEqualByComparingTo(new BigDecimal("8000.00"));
            assertThat(result.getMotif()).isEqualTo("Dommages validés");
            assertThat(result.getNumeroVirement()).isEqualTo("VIR-001");
            assertThat(result.getDatePaiement()).isEqualTo(LocalDate.of(2024, 6, 5));
            assertThat(result.getSuperviseur()).isEqualTo(supEntity);

            verify(utilisateurMapper).toEntity(supDomain);
        }

        @Test
        @DisplayName("Devrait retourner null si le domain est null")
        void shouldReturnNull_WhenDomainIsNull() {
            DecisionEntity result = decisionMapper.toEntity(null);

            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper);
        }
    }
}