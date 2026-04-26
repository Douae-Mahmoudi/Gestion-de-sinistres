package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.model.Decision;
import com.SinistraPro.domain.model.Rapport;
import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.StatutSinistre;
import com.SinistraPro.domain.infrastructure.persistence.entity.DecisionEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.RapportEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SinistreMapper")
class SinistreMapperTest {

    @Mock private UtilisateurMapper utilisateurMapper;
    @Mock private RapportMapper     rapportMapper;
    @Mock private DecisionMapper    decisionMapper;

    @InjectMocks
    private SinistreMapper sinistreMapper;

    private static final Long           ID               = 1L;
    private static final String         NUMERO           = "SIN-2024-001";
    private static final String         DESCRIPTION      = "Accident de voiture sur autoroute";
    private static final LocalDate      DATE_INCIDENT    = LocalDate.of(2024, 3, 20);
    private static final String         LIEU_INCIDENT    = "Casablanca, A1";
    private static final String         NUMERO_POLICE    = "POL-789456";
    private static final String         NUMERO_CONSTAT   = "CONST-123";
    private static final StatutSinistre STATUT           = StatutSinistre.EN_EXPERTISE;
    private static final LocalDateTime  DATE_DECLARATION = LocalDateTime.of(2024, 3, 21, 8, 0);

    private Utilisateur clientDomain;
    private Utilisateur agentDomain;
    private Utilisateur expertDomain;
    private Rapport     rapportDomain;
    private Decision    decisionDomain;

    private UtilisateurEntity clientEntity;
    private UtilisateurEntity agentEntity;
    private UtilisateurEntity expertEntity;
    private RapportEntity     rapportEntity;
    private DecisionEntity    decisionEntity;

    private SinistreEntity sinistreEntity;
    private Sinistre       sinistre;

    @BeforeEach
    void setUp() {
        clientDomain   = Utilisateur.builder().id(10L).nom("Client").role(Role.CLIENT).build();
        agentDomain    = Utilisateur.builder().id(11L).nom("Agent").role(Role.AGENT).build();
        expertDomain   = Utilisateur.builder().id(12L).nom("Expert").role(Role.EXPERT).build();
        rapportDomain  = Rapport.builder().id(20L).observations("RAS").build();
        decisionDomain = Decision.builder().id(30L).build();

        clientEntity   = UtilisateurEntity.builder().id(10L).nom("Client").role(Role.CLIENT).build();
        agentEntity    = UtilisateurEntity.builder().id(11L).nom("Agent").role(Role.AGENT).build();
        expertEntity   = UtilisateurEntity.builder().id(12L).nom("Expert").role(Role.EXPERT).build();
        rapportEntity  = RapportEntity.builder().id(20L).observations("RAS").build();
        decisionEntity = DecisionEntity.builder().id(30L).build();

        sinistreEntity = SinistreEntity.builder()
                .id(ID)
                .numero(NUMERO)
                .description(DESCRIPTION)
                .dateIncident(DATE_INCIDENT)
                .lieuIncident(LIEU_INCIDENT)
                .numeroPolicAssurance(NUMERO_POLICE)
                .numeroConstatAmiable(NUMERO_CONSTAT)
                .statut(STATUT)
                .dateDeclaration(DATE_DECLARATION)
                .client(clientEntity)
                .agent(agentEntity)
                .expert(expertEntity)
                .rapport(rapportEntity)
                .decision(decisionEntity)
                .build();

        sinistre = Sinistre.builder()
                .id(ID)
                .numero(NUMERO)
                .description(DESCRIPTION)
                .dateIncident(DATE_INCIDENT)
                .lieuIncident(LIEU_INCIDENT)
                .numeroPolicAssurance(NUMERO_POLICE)
                .numeroConstatAmiable(NUMERO_CONSTAT)
                .statut(STATUT)
                .dateDeclaration(DATE_DECLARATION)
                .client(clientDomain)
                .agent(agentDomain)
                .expert(expertDomain)
                .rapport(rapportDomain)
                .decision(decisionDomain)
                .build();
    }

    @Nested
    @DisplayName("toDomain(SinistreEntity)")
    class ToDomainTests {

        @Test
        @DisplayName("retourne null si l'entité est null")
        void shouldReturnNullWhenEntityIsNull() {
            Sinistre result = sinistreMapper.toDomain(null);
            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper, rapportMapper, decisionMapper);
        }

        @Test
        @DisplayName("mappe tous les champs scalaires correctement")
        void shouldMapAllScalarFields() {
            stubToDomainDependencies();

            Sinistre result = sinistreMapper.toDomain(sinistreEntity);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getNumero()).isEqualTo(NUMERO);
            assertThat(result.getDescription()).isEqualTo(DESCRIPTION);
            assertThat(result.getDateIncident()).isEqualTo(DATE_INCIDENT);
            assertThat(result.getLieuIncident()).isEqualTo(LIEU_INCIDENT);
            assertThat(result.getNumeroPolicAssurance()).isEqualTo(NUMERO_POLICE);
            assertThat(result.getNumeroConstatAmiable()).isEqualTo(NUMERO_CONSTAT);
            assertThat(result.getStatut()).isEqualTo(STATUT);
            assertThat(result.getDateDeclaration()).isEqualTo(DATE_DECLARATION);
        }

        @Test
        @DisplayName("délègue le mapping du client, agent et expert à UtilisateurMapper")
        void shouldDelegateUtilisateurMappings() {
            stubToDomainDependencies();

            Sinistre result = sinistreMapper.toDomain(sinistreEntity);

            verify(utilisateurMapper).toDomain(clientEntity);
            verify(utilisateurMapper).toDomain(agentEntity);
            verify(utilisateurMapper).toDomain(expertEntity);
            assertThat(result.getClient()).isSameAs(clientDomain);
            assertThat(result.getAgent()).isSameAs(agentDomain);
            assertThat(result.getExpert()).isSameAs(expertDomain);
        }

        @Test
        @DisplayName("délègue le mapping du rapport à RapportMapper")
        void shouldDelegateRapportMapping() {
            stubToDomainDependencies();

            Sinistre result = sinistreMapper.toDomain(sinistreEntity);

            verify(rapportMapper).toDomain(rapportEntity);
            assertThat(result.getRapport()).isSameAs(rapportDomain);
        }

        @Test
        @DisplayName("délègue le mapping de la décision à DecisionMapper")
        void shouldDelegateDecisionMapping() {
            stubToDomainDependencies();

            Sinistre result = sinistreMapper.toDomain(sinistreEntity);

            verify(decisionMapper).toDomain(decisionEntity);
            assertThat(result.getDecision()).isSameAs(decisionDomain);
        }

        @Test
        @DisplayName("mappe correctement un sinistre sans rapport ni décision")
        void shouldHandleNullRapportAndDecision() {
            sinistreEntity = SinistreEntity.builder()
                    .id(ID).numero(NUMERO).statut(STATUT)
                    .client(clientEntity).agent(agentEntity).expert(expertEntity)
                    .rapport(null).decision(null)
                    .build();

            when(utilisateurMapper.toDomain(clientEntity)).thenReturn(clientDomain);
            when(utilisateurMapper.toDomain(agentEntity)).thenReturn(agentDomain);
            when(utilisateurMapper.toDomain(expertEntity)).thenReturn(expertDomain);
            when(rapportMapper.toDomain(null)).thenReturn(null);
            when(decisionMapper.toDomain(null)).thenReturn(null);

            Sinistre result = sinistreMapper.toDomain(sinistreEntity);

            assertThat(result.getRapport()).isNull();
            assertThat(result.getDecision()).isNull();
        }

        @Test
        @DisplayName("mappe correctement un sinistre sans client, agent ni expert")
        void shouldHandleNullUtilisateurs() {
            sinistreEntity = SinistreEntity.builder()
                    .id(ID).numero(NUMERO).statut(STATUT)
                    .client(null).agent(null).expert(null)
                    .rapport(rapportEntity).decision(decisionEntity)
                    .build();

            when(utilisateurMapper.toDomain(null)).thenReturn(null);
            when(rapportMapper.toDomain(rapportEntity)).thenReturn(rapportDomain);
            when(decisionMapper.toDomain(decisionEntity)).thenReturn(decisionDomain);

            Sinistre result = sinistreMapper.toDomain(sinistreEntity);

            assertThat(result.getClient()).isNull();
            assertThat(result.getAgent()).isNull();
            assertThat(result.getExpert()).isNull();
        }

        @Test
        @DisplayName("mappe les champs scalaires null sans NullPointerException")
        void shouldHandleNullScalarFields() {
            SinistreEntity minimal = SinistreEntity.builder()
                    .id(ID)
                    .numero(null).description(null).dateIncident(null)
                    .lieuIncident(null).numeroPolicAssurance(null)
                    .numeroConstatAmiable(null).statut(null).dateDeclaration(null)
                    .client(null).agent(null).expert(null)
                    .rapport(null).decision(null)
                    .build();

            when(utilisateurMapper.toDomain(null)).thenReturn(null);
            when(rapportMapper.toDomain(null)).thenReturn(null);
            when(decisionMapper.toDomain(null)).thenReturn(null);

            Sinistre result = sinistreMapper.toDomain(minimal);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getNumero()).isNull();
            assertThat(result.getStatut()).isNull();
        }

        @Test
        @DisplayName("mappe correctement chaque valeur de StatutSinistre")
        void shouldMapEachStatutSinistre() {
            for (StatutSinistre statut : StatutSinistre.values()) {
                SinistreEntity e = SinistreEntity.builder()
                        .id(ID).statut(statut)
                        .client(null).agent(null).expert(null)
                        .rapport(null).decision(null)
                        .build();

                when(utilisateurMapper.toDomain(null)).thenReturn(null);
                when(rapportMapper.toDomain(null)).thenReturn(null);
                when(decisionMapper.toDomain(null)).thenReturn(null);

                Sinistre result = sinistreMapper.toDomain(e);
                assertThat(result.getStatut()).isEqualTo(statut);
            }
        }
    }

    @Nested
    @DisplayName("toEntity(Sinistre)")
    class ToEntityTests {

        @Test
        @DisplayName("retourne null si le domaine est null")
        void shouldReturnNullWhenDomainIsNull() {
            SinistreEntity result = sinistreMapper.toEntity(null);
            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper, rapportMapper, decisionMapper);
        }

        @Test
        @DisplayName("mappe tous les champs scalaires correctement")
        void shouldMapAllScalarFields() {
            stubToEntityDependencies();

            SinistreEntity result = sinistreMapper.toEntity(sinistre);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getNumero()).isEqualTo(NUMERO);
            assertThat(result.getDescription()).isEqualTo(DESCRIPTION);
            assertThat(result.getDateIncident()).isEqualTo(DATE_INCIDENT);
            assertThat(result.getLieuIncident()).isEqualTo(LIEU_INCIDENT);
            assertThat(result.getNumeroPolicAssurance()).isEqualTo(NUMERO_POLICE);
            assertThat(result.getNumeroConstatAmiable()).isEqualTo(NUMERO_CONSTAT);
            assertThat(result.getStatut()).isEqualTo(STATUT);
            assertThat(result.getDateDeclaration()).isEqualTo(DATE_DECLARATION);
        }

        @Test
        @DisplayName("délègue le mapping du client, agent et expert à UtilisateurMapper")
        void shouldDelegateUtilisateurMappings() {
            stubToEntityDependencies();

            SinistreEntity result = sinistreMapper.toEntity(sinistre);

            verify(utilisateurMapper).toEntity(clientDomain);
            verify(utilisateurMapper).toEntity(agentDomain);
            verify(utilisateurMapper).toEntity(expertDomain);
            assertThat(result.getClient()).isSameAs(clientEntity);
            assertThat(result.getAgent()).isSameAs(agentEntity);
            assertThat(result.getExpert()).isSameAs(expertEntity);
        }

        @Test
        @DisplayName("délègue le mapping du rapport à RapportMapper")
        void shouldDelegateRapportMapping() {
            stubToEntityDependencies();

            SinistreEntity result = sinistreMapper.toEntity(sinistre);

            verify(rapportMapper).toEntity(rapportDomain);
            assertThat(result.getRapport()).isSameAs(rapportEntity);
        }

        @Test
        @DisplayName("délègue le mapping de la décision à DecisionMapper")
        void shouldDelegateDecisionMapping() {
            stubToEntityDependencies();

            SinistreEntity result = sinistreMapper.toEntity(sinistre);

            verify(decisionMapper).toEntity(decisionDomain);
            assertThat(result.getDecision()).isSameAs(decisionEntity);
        }

        @Test
        @DisplayName("mappe correctement un sinistre sans rapport ni décision")
        void shouldHandleNullRapportAndDecision() {
            sinistre = Sinistre.builder()
                    .id(ID).numero(NUMERO).statut(STATUT)
                    .client(clientDomain).agent(agentDomain).expert(expertDomain)
                    .rapport(null).decision(null)
                    .build();

            when(utilisateurMapper.toEntity(clientDomain)).thenReturn(clientEntity);
            when(utilisateurMapper.toEntity(agentDomain)).thenReturn(agentEntity);
            when(utilisateurMapper.toEntity(expertDomain)).thenReturn(expertEntity);
            when(rapportMapper.toEntity(null)).thenReturn(null);
            when(decisionMapper.toEntity(null)).thenReturn(null);

            SinistreEntity result = sinistreMapper.toEntity(sinistre);

            assertThat(result.getRapport()).isNull();
            assertThat(result.getDecision()).isNull();
        }

        @Test
        @DisplayName("mappe correctement un sinistre sans client, agent ni expert")
        void shouldHandleNullUtilisateurs() {
            sinistre = Sinistre.builder()
                    .id(ID).numero(NUMERO).statut(STATUT)
                    .client(null).agent(null).expert(null)
                    .rapport(rapportDomain).decision(decisionDomain)
                    .build();

            when(utilisateurMapper.toEntity(null)).thenReturn(null);
            when(rapportMapper.toEntity(rapportDomain)).thenReturn(rapportEntity);
            when(decisionMapper.toEntity(decisionDomain)).thenReturn(decisionEntity);

            SinistreEntity result = sinistreMapper.toEntity(sinistre);

            assertThat(result.getClient()).isNull();
            assertThat(result.getAgent()).isNull();
            assertThat(result.getExpert()).isNull();
        }
    }

    @Nested
    @DisplayName("Round-trip toDomain → toEntity")
    class RoundTripTests {

        @Test
        @DisplayName("entity → domain → entity préserve tous les champs scalaires et relations")
        void shouldPreserveAllDataInRoundTrip() {
            stubToDomainDependencies();
            when(utilisateurMapper.toEntity(clientDomain)).thenReturn(clientEntity);
            when(utilisateurMapper.toEntity(agentDomain)).thenReturn(agentEntity);
            when(utilisateurMapper.toEntity(expertDomain)).thenReturn(expertEntity);
            when(rapportMapper.toEntity(rapportDomain)).thenReturn(rapportEntity);
            when(decisionMapper.toEntity(decisionDomain)).thenReturn(decisionEntity);

            Sinistre       domain      = sinistreMapper.toDomain(sinistreEntity);
            SinistreEntity reconverted = sinistreMapper.toEntity(domain);

            assertThat(reconverted.getId()).isEqualTo(sinistreEntity.getId());
            assertThat(reconverted.getNumero()).isEqualTo(sinistreEntity.getNumero());
            assertThat(reconverted.getDescription()).isEqualTo(sinistreEntity.getDescription());
            assertThat(reconverted.getDateIncident()).isEqualTo(sinistreEntity.getDateIncident());
            assertThat(reconverted.getLieuIncident()).isEqualTo(sinistreEntity.getLieuIncident());
            assertThat(reconverted.getNumeroPolicAssurance()).isEqualTo(sinistreEntity.getNumeroPolicAssurance());
            assertThat(reconverted.getNumeroConstatAmiable()).isEqualTo(sinistreEntity.getNumeroConstatAmiable());
            assertThat(reconverted.getStatut()).isEqualTo(sinistreEntity.getStatut());
            assertThat(reconverted.getDateDeclaration()).isEqualTo(sinistreEntity.getDateDeclaration());
            assertThat(reconverted.getClient()).isSameAs(clientEntity);
            assertThat(reconverted.getAgent()).isSameAs(agentEntity);
            assertThat(reconverted.getExpert()).isSameAs(expertEntity);
            assertThat(reconverted.getRapport()).isSameAs(rapportEntity);
            assertThat(reconverted.getDecision()).isSameAs(decisionEntity);
        }
    }

    private void stubToDomainDependencies() {
        when(utilisateurMapper.toDomain(clientEntity)).thenReturn(clientDomain);
        when(utilisateurMapper.toDomain(agentEntity)).thenReturn(agentDomain);
        when(utilisateurMapper.toDomain(expertEntity)).thenReturn(expertDomain);
        when(rapportMapper.toDomain(rapportEntity)).thenReturn(rapportDomain);
        when(decisionMapper.toDomain(decisionEntity)).thenReturn(decisionDomain);
    }

    private void stubToEntityDependencies() {
        when(utilisateurMapper.toEntity(clientDomain)).thenReturn(clientEntity);
        when(utilisateurMapper.toEntity(agentDomain)).thenReturn(agentEntity);
        when(utilisateurMapper.toEntity(expertDomain)).thenReturn(expertEntity);
        when(rapportMapper.toEntity(rapportDomain)).thenReturn(rapportEntity);
        when(decisionMapper.toEntity(decisionDomain)).thenReturn(decisionEntity);
    }
}