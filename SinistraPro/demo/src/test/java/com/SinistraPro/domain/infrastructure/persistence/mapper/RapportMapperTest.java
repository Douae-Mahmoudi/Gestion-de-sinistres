package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.model.Rapport;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.infrastructure.persistence.entity.RapportEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RapportMapper")
class RapportMapperTest {

    @Mock
    private UtilisateurMapper utilisateurMapper;

    @InjectMocks
    private RapportMapper rapportMapper;


    private static final Long        ID                  = 1L;
    private static final String      DESCRIPTION_DOMMAGES = "Dommages au pare-chocs avant";
    private static final BigDecimal  MONTANT_ESTIME      = new BigDecimal("3500.00");
    private static final String      OBSERVATIONS        = "Choc frontal modéré";
    private static final LocalDateTime DATE_SOUMISSION   = LocalDateTime.of(2024, 6, 15, 10, 30);

    private Utilisateur      expertDomain;
    private UtilisateurEntity expertEntity;
    private RapportEntity    rapportEntity;
    private Rapport          rapport;

    @BeforeEach
    void setUp() {
        expertDomain = Utilisateur.builder()
                .id(10L)
                .nom("Dupont")
                .build();

        expertEntity = UtilisateurEntity.builder()
                .id(10L)
                .nom("Dupont")
                .build();

        rapportEntity = RapportEntity.builder()
                .id(ID)
                .descriptionDommages(DESCRIPTION_DOMMAGES)
                .montantEstime(MONTANT_ESTIME)
                .observations(OBSERVATIONS)
                .dateSoumission(DATE_SOUMISSION)
                .expert(expertEntity)
                .build();

        rapport = Rapport.builder()
                .id(ID)
                .descriptionDommages(DESCRIPTION_DOMMAGES)
                .montantEstime(MONTANT_ESTIME)
                .observations(OBSERVATIONS)
                .dateSoumission(DATE_SOUMISSION)
                .expert(expertDomain)
                .build();
    }


    @Nested
    @DisplayName("toDomain(RapportEntity)")
    class ToDomainTests {

        @Test
        @DisplayName("retourne null si l'entité est null")
        void shouldReturnNullWhenEntityIsNull() {
            Rapport result = rapportMapper.toDomain(null);
            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper);
        }

        @Test
        @DisplayName("mappe tous les champs scalaires correctement")
        void shouldMapAllScalarFields() {
            when(utilisateurMapper.toDomain(expertEntity)).thenReturn(expertDomain);

            Rapport result = rapportMapper.toDomain(rapportEntity);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getDescriptionDommages()).isEqualTo(DESCRIPTION_DOMMAGES);
            assertThat(result.getMontantEstime()).isEqualByComparingTo(MONTANT_ESTIME);
            assertThat(result.getObservations()).isEqualTo(OBSERVATIONS);
            assertThat(result.getDateSoumission()).isEqualTo(DATE_SOUMISSION);
        }

        @Test
        @DisplayName("délègue le mapping de l'expert à UtilisateurMapper")
        void shouldDelegateExpertMappingToUtilisateurMapper() {
            when(utilisateurMapper.toDomain(expertEntity)).thenReturn(expertDomain);

            Rapport result = rapportMapper.toDomain(rapportEntity);

            verify(utilisateurMapper, times(1)).toDomain(expertEntity);
            assertThat(result.getExpert()).isSameAs(expertDomain);
        }

        @Test
        @DisplayName("mappe un expert null via UtilisateurMapper")
        void shouldHandleNullExpertViaMapper() {
            rapportEntity = RapportEntity.builder()
                    .id(ID)
                    .descriptionDommages(DESCRIPTION_DOMMAGES)
                    .montantEstime(MONTANT_ESTIME)
                    .observations(OBSERVATIONS)
                    .dateSoumission(DATE_SOUMISSION)
                    .expert(null)
                    .build();

            when(utilisateurMapper.toDomain(null)).thenReturn(null);

            Rapport result = rapportMapper.toDomain(rapportEntity);

            assertThat(result).isNotNull();
            assertThat(result.getExpert()).isNull();
            verify(utilisateurMapper).toDomain(null);
        }

        @Test
        @DisplayName("mappe correctement des champs null optionnels")
        void shouldMapNullOptionalFields() {
            RapportEntity entityPartielle = RapportEntity.builder()
                    .id(ID)
                    .descriptionDommages(null)
                    .montantEstime(null)
                    .observations(null)
                    .dateSoumission(null)
                    .expert(expertEntity)
                    .build();

            when(utilisateurMapper.toDomain(expertEntity)).thenReturn(expertDomain);

            Rapport result = rapportMapper.toDomain(entityPartielle);

            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getDescriptionDommages()).isNull();
            assertThat(result.getMontantEstime()).isNull();
            assertThat(result.getObservations()).isNull();
            assertThat(result.getDateSoumission()).isNull();
        }
    }


    @Nested
    @DisplayName("toEntity(Rapport)")
    class ToEntityTests {

        @Test
        @DisplayName("retourne null si le domaine est null")
        void shouldReturnNullWhenDomainIsNull() {
            RapportEntity result = rapportMapper.toEntity(null);
            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper);
        }

        @Test
        @DisplayName("mappe tous les champs scalaires correctement")
        void shouldMapAllScalarFields() {
            when(utilisateurMapper.toEntity(expertDomain)).thenReturn(expertEntity);

            RapportEntity result = rapportMapper.toEntity(rapport);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getDescriptionDommages()).isEqualTo(DESCRIPTION_DOMMAGES);
            assertThat(result.getMontantEstime()).isEqualByComparingTo(MONTANT_ESTIME);
            assertThat(result.getObservations()).isEqualTo(OBSERVATIONS);
            assertThat(result.getDateSoumission()).isEqualTo(DATE_SOUMISSION);
        }

        @Test
        @DisplayName("délègue le mapping de l'expert à UtilisateurMapper")
        void shouldDelegateExpertMappingToUtilisateurMapper() {
            when(utilisateurMapper.toEntity(expertDomain)).thenReturn(expertEntity);

            RapportEntity result = rapportMapper.toEntity(rapport);

            verify(utilisateurMapper, times(1)).toEntity(expertDomain);
            assertThat(result.getExpert()).isSameAs(expertEntity);
        }

        @Test
        @DisplayName("mappe un expert null via UtilisateurMapper")
        void shouldHandleNullExpertViaMapper() {
            Rapport rapportSansExpert = Rapport.builder()
                    .id(ID)
                    .descriptionDommages(DESCRIPTION_DOMMAGES)
                    .montantEstime(MONTANT_ESTIME)
                    .observations(OBSERVATIONS)
                    .dateSoumission(DATE_SOUMISSION)
                    .expert(null)
                    .build();

            when(utilisateurMapper.toEntity(null)).thenReturn(null);

            RapportEntity result = rapportMapper.toEntity(rapportSansExpert);

            assertThat(result).isNotNull();
            assertThat(result.getExpert()).isNull();
            verify(utilisateurMapper).toEntity(null);
        }

        @Test
        @DisplayName("mappe correctement des champs null optionnels")
        void shouldMapNullOptionalFields() {
            Rapport rapportPartiel = Rapport.builder()
                    .id(ID)
                    .descriptionDommages(null)
                    .montantEstime(null)
                    .observations(null)
                    .dateSoumission(null)
                    .expert(expertDomain)
                    .build();

            when(utilisateurMapper.toEntity(expertDomain)).thenReturn(expertEntity);

            RapportEntity result = rapportMapper.toEntity(rapportPartiel);

            assertThat(result.getId()).isEqualTo(ID);
            assertThat(result.getDescriptionDommages()).isNull();
            assertThat(result.getMontantEstime()).isNull();
            assertThat(result.getObservations()).isNull();
            assertThat(result.getDateSoumission()).isNull();
        }
    }


    @Nested
    @DisplayName("Round-trip toDomain  toEntity")
    class RoundTripTests {

        @Test
        @DisplayName("entity domain entity préserve toutes les données")
        void shouldPreserveDataInRoundTrip() {
            when(utilisateurMapper.toDomain(expertEntity)).thenReturn(expertDomain);
            when(utilisateurMapper.toEntity(expertDomain)).thenReturn(expertEntity);

            Rapport domain  = rapportMapper.toDomain(rapportEntity);
            RapportEntity reconverted = rapportMapper.toEntity(domain);

            assertThat(reconverted.getId()).isEqualTo(rapportEntity.getId());
            assertThat(reconverted.getDescriptionDommages()).isEqualTo(rapportEntity.getDescriptionDommages());
            assertThat(reconverted.getMontantEstime()).isEqualByComparingTo(rapportEntity.getMontantEstime());
            assertThat(reconverted.getObservations()).isEqualTo(rapportEntity.getObservations());
            assertThat(reconverted.getDateSoumission()).isEqualTo(rapportEntity.getDateSoumission());
        }
    }
}
