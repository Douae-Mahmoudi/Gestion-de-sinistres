package com.SinistraPro.domain.infrastructure.persistence.mapper;

import com.SinistraPro.domain.infrastructure.persistence.entity.HistoriqueEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.model.Historique;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.StatutSinistre;
import com.SinistraPro.domain.model.Utilisateur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoriqueMapperTest {

    @Mock
    private UtilisateurMapper utilisateurMapper;

    @InjectMocks
    private HistoriqueMapper historiqueMapper;


    private UtilisateurEntity buildAgentEntity() {
        UtilisateurEntity u = new UtilisateurEntity();
        u.setId(7L);
        u.setNom("Agent"); u.setPrenom("X");
        u.setEmail("agent@mail.ma");
        u.setRole(Role.AGENT);
        u.setMotDePasse("secret");
        u.setDateCreation(LocalDateTime.now());
        return u;
    }

    private Utilisateur buildAgentDomain() {
        return Utilisateur.builder()
                .id(7L).nom("Agent").prenom("X")
                .email("agent@mail.ma").role(Role.AGENT)
                .build();
    }

    private SinistreEntity buildSinistreEntity() {
        SinistreEntity s = new SinistreEntity();
        s.setId(30L);
        s.setNumero("SIN-H001");
        s.setStatut(StatutSinistre.DECLARE);
        s.setTypeSinistre("ACCIDENT");
        return s;
    }

    private HistoriqueEntity buildHistoriqueEntity(UtilisateurEntity agent,
                                                   SinistreEntity sinistre) {
        HistoriqueEntity h = new HistoriqueEntity();
        h.setId(1L);
        h.setAncienStatut(StatutSinistre.DECLARE);
        h.setNouveauStatut(StatutSinistre.AFFECTE);
        h.setCommentaire("Affectation agent");
        h.setDateAction(LocalDateTime.of(2024, 3, 1, 8, 0));
        h.setEffectuePar(agent);
        h.setSinistre(sinistre);
        return h;
    }

    private Historique buildHistoriqueDomain(Utilisateur agent) {
        return Historique.builder()
                .id(1L)
                .sinistreId(30L)
                .ancienStatut(StatutSinistre.DECLARE)
                .nouveauStatut(StatutSinistre.AFFECTE)
                .commentaire("Affectation agent")
                .dateAction(LocalDateTime.of(2024, 3, 1, 8, 0))
                .effectuePar(agent)
                .build();
    }

    // ─── toDomain ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("Devrait mapper tous les champs de l'entity vers le domain")
        void shouldMapAllFields_FromEntityToDomain() {
            UtilisateurEntity agentEntity = buildAgentEntity();
            Utilisateur agentDomain = buildAgentDomain();
            SinistreEntity sinistre = buildSinistreEntity();
            HistoriqueEntity entity = buildHistoriqueEntity(agentEntity, sinistre);

            when(utilisateurMapper.toDomain(agentEntity)).thenReturn(agentDomain);

            Historique result = historiqueMapper.toDomain(entity);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getSinistreId()).isEqualTo(30L);
            assertThat(result.getAncienStatut()).isEqualTo(StatutSinistre.DECLARE);
            assertThat(result.getNouveauStatut()).isEqualTo(StatutSinistre.AFFECTE);
            assertThat(result.getCommentaire()).isEqualTo("Affectation agent");
            assertThat(result.getDateAction()).isEqualTo(LocalDateTime.of(2024, 3, 1, 8, 0));
            assertThat(result.getEffectuePar()).isEqualTo(agentDomain);

            verify(utilisateurMapper).toDomain(agentEntity);
        }

        @Test
        @DisplayName("Devrait retourner null si l'entity est null")
        void shouldReturnNull_WhenEntityIsNull() {
            Historique result = historiqueMapper.toDomain(null);

            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper);
        }

        @Test
        @DisplayName("Devrait mapper ancienStatut null correctement")
        void shouldMapNullAncienStatut() {
            UtilisateurEntity agentEntity = buildAgentEntity();
            SinistreEntity sinistre = buildSinistreEntity();
            HistoriqueEntity entity = buildHistoriqueEntity(agentEntity, sinistre);
            entity.setAncienStatut(null);

            when(utilisateurMapper.toDomain(agentEntity)).thenReturn(buildAgentDomain());

            Historique result = historiqueMapper.toDomain(entity);

            assertThat(result.getAncienStatut()).isNull();
            assertThat(result.getNouveauStatut()).isEqualTo(StatutSinistre.AFFECTE);
        }
    }


    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("Devrait mapper tous les champs du domain vers l'entity avec le sinistre")
        void shouldMapAllFields_FromDomainToEntity() {
            Utilisateur agentDomain = buildAgentDomain();
            UtilisateurEntity agentEntity = buildAgentEntity();
            SinistreEntity sinistreEntity = buildSinistreEntity();
            Historique domain = buildHistoriqueDomain(agentDomain);

            when(utilisateurMapper.toEntity(agentDomain)).thenReturn(agentEntity);

            HistoriqueEntity result = historiqueMapper.toEntity(domain, sinistreEntity);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getAncienStatut()).isEqualTo(StatutSinistre.DECLARE);
            assertThat(result.getNouveauStatut()).isEqualTo(StatutSinistre.AFFECTE);
            assertThat(result.getCommentaire()).isEqualTo("Affectation agent");
            assertThat(result.getDateAction()).isEqualTo(LocalDateTime.of(2024, 3, 1, 8, 0));
            assertThat(result.getSinistre()).isEqualTo(sinistreEntity);
            assertThat(result.getEffectuePar()).isEqualTo(agentEntity);

            verify(utilisateurMapper).toEntity(agentDomain);
        }

        @Test
        @DisplayName("Devrait retourner null si le domain est null")
        void shouldReturnNull_WhenDomainIsNull() {
            HistoriqueEntity result = historiqueMapper.toEntity(null, buildSinistreEntity());

            assertThat(result).isNull();
            verifyNoInteractions(utilisateurMapper);
        }

        @Test
        @DisplayName("Devrait lier correctement le sinistre passé en paramètre")
        void shouldLinkSinistre_PassedAsParameter() {
            Utilisateur agentDomain = buildAgentDomain();
            SinistreEntity sinistreEntity = buildSinistreEntity();
            Historique domain = buildHistoriqueDomain(agentDomain);

            when(utilisateurMapper.toEntity(agentDomain)).thenReturn(buildAgentEntity());

            HistoriqueEntity result = historiqueMapper.toEntity(domain, sinistreEntity);

            assertThat(result.getSinistre().getId()).isEqualTo(30L);
            assertThat(result.getSinistre().getNumero()).isEqualTo("SIN-H001");
        }
    }
}