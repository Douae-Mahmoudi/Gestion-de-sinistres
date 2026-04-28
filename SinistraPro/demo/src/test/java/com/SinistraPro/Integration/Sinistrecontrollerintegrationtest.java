package com.SinistraPro.Integration;

import com.SinistraPro.domain.application.service.NotificationService;
import com.SinistraPro.domain.exposition.controller.SinistreController;
import com.SinistraPro.domain.exposition.dto.request.*;
import com.SinistraPro.domain.exposition.dto.response.SinistreResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.*;
import com.SinistraPro.domain.port.in.*;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SinistreController.class)
@DisplayName("Tests d'intégration – SinistreController")
class SinistreControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;

    @MockBean private DeclarerSinistreUseCase  declarerUseCase;
    @MockBean private AffecterExpertUseCase    affecterUseCase;
    @MockBean private SoumettreRapportUseCase  soumettreRapportUseCase;
    @MockBean private ApprouverSinistreUseCase approuverUseCase;
    @MockBean private RejeterSinistreUseCase   rejeterUseCase;
    @MockBean private CloturerSinistreUseCase  cloturerUseCase;

    @MockBean private SinistreRepositoryPort    sinistreRepository;
    @MockBean private UtilisateurRepositoryPort utilisateurRepository;
    @MockBean private SinistreDtoMapper         dtoMapper;
    @MockBean private NotificationService       notificationService;

    private ObjectMapper     objectMapper;
    private Utilisateur      clientUser;
    private Utilisateur      agentUser;
    private Utilisateur      expertUser;
    private Utilisateur      superviseurUser;
    private Sinistre         sinistreBase;
    private SinistreResponse sinistreResponseBase;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        clientUser = Utilisateur.builder()
                .id(1L).nom("Dupont").prenom("Jean")
                .email("client@test.com").role(Role.CLIENT).build();

        agentUser = Utilisateur.builder()
                .id(2L).nom("Agent").prenom("Rachid")
                .email("agent@test.com").role(Role.AGENT).build();

        expertUser = Utilisateur.builder()
                .id(3L).nom("Expert").prenom("Karim")
                .email("expert@test.com").role(Role.EXPERT).build();

        superviseurUser = Utilisateur.builder()
                .id(4L).nom("Super").prenom("Amina")
                .email("superviseur@test.com").role(Role.SUPERVISEUR).build();

        sinistreBase = Sinistre.builder()
                .id(1L)
                .numero("SP-001")
                .typeSinistre("Accident")
                .description("Collision frontale")
                .dateIncident(LocalDate.of(2024, 6, 15))
                .lieuIncident("Casablanca")
                .numeroPolicAssurance("POL-123")
                .statut(StatutSinistre.DECLARE)
                .dateDeclaration(LocalDateTime.now())
                .client(clientUser)
                .build();

        sinistreResponseBase = SinistreResponse.builder()
                .id(1L)
                .numero("SP-001")
                .typeSinistre("Accident")
                .description("Collision frontale")
                .statut("DECLARE")
                .build();
    }


    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("POST /api/sinistres – 201 Created quand la déclaration est valide")
    void declarer_shouldReturn201_whenRequestIsValid() throws Exception {

        DeclarationSinistreRequest request = new DeclarationSinistreRequest();
        request.setTypeSinistre("Accident");
        request.setDescription("Collision frontale");
        request.setDateIncident(LocalDate.of(2024, 6, 15));
        request.setLieuIncident("Casablanca");
        request.setNumeroPolicAssurance("POL-123");
        request.setNumeroConstatAmiable("CONST-2024-001");

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(declarerUseCase.declarer(any(Sinistre.class)))
                .thenReturn(sinistreBase);
        when(dtoMapper.toResponse(sinistreBase))
                .thenReturn(sinistreResponseBase);
        doNothing().when(notificationService)
                .creer(any(), any(), anyString(), anyString());

        mockMvc.perform(post("/api/sinistres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("SP-001"))
                .andExpect(jsonPath("$.statut").value("DECLARE"));

        verify(declarerUseCase, times(1)).declarer(any(Sinistre.class));
        verify(notificationService, times(1))
                .creer(eq(clientUser), eq(sinistreBase), anyString(), eq("STATUT_CHANGE"));
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("POST /api/sinistres – 400 Bad Request quand le body est vide")
    void declarer_shouldReturn400_whenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/api/sinistres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/sinistres – 401/403 quand non authentifié")
    void declarer_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/sinistres")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @WithMockUser(username = "agent@test.com", roles = "AGENT")
    @DisplayName("PUT /api/sinistres/{id}/affecter – 200 OK quand l'affectation est valide")
    void affecter_shouldReturn200_whenValid() throws Exception {

        Sinistre sinistreAffecte = Sinistre.builder()
                .id(1L).numero("SP-001").statut(StatutSinistre.AFFECTE)
                .client(clientUser).expert(expertUser).build();

        SinistreResponse responseAffecte = SinistreResponse.builder()
                .id(1L).numero("SP-001").statut("AFFECTE").build();

        AffectationRequest request = new AffectationRequest();
        request.setExpertId(3L);
        request.setCommentaireAgent("Expertise urgente requise");

        when(utilisateurRepository.findByEmail("agent@test.com"))
                .thenReturn(Optional.of(agentUser));
        when(affecterUseCase.affecter(eq(1L), eq(3L), anyString(), eq(agentUser)))
                .thenReturn(sinistreAffecte);
        when(dtoMapper.toResponse(sinistreAffecte))
                .thenReturn(responseAffecte);
        doNothing().when(notificationService)
                .creer(any(), any(), anyString(), anyString());

        mockMvc.perform(put("/api/sinistres/1/affecter")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("AFFECTE"));

        verify(notificationService, times(2))
                .creer(any(), any(), anyString(), anyString());
    }



    @Test
    @WithMockUser(username = "expert@test.com", roles = "EXPERT")
    @DisplayName("PUT /api/sinistres/{id}/soumettre-rapport – 200 OK avec rapport valide")
    void soumettreRapport_shouldReturn200_whenValid() throws Exception {

        Sinistre sinistreEvalue = Sinistre.builder()
                .id(1L).numero("SP-001").statut(StatutSinistre.EVALUE)
                .client(clientUser).expert(expertUser).build();

        SinistreResponse responseEvalue = SinistreResponse.builder()
                .id(1L).numero("SP-001").statut("EVALUE").build();

        RapportExpertRequest request = new RapportExpertRequest();
        request.setDescriptionDommages("Dommages importants sur la carrosserie");
        request.setMontantEstime(BigDecimal.valueOf(15000));
        request.setObservations("Aucune fraude détectée");

        when(utilisateurRepository.findByEmail("expert@test.com"))
                .thenReturn(Optional.of(expertUser));
        when(soumettreRapportUseCase.soumettre(eq(1L), any(Rapport.class)))
                .thenReturn(sinistreEvalue);
        when(dtoMapper.toResponse(sinistreEvalue))
                .thenReturn(responseEvalue);
        when(utilisateurRepository.findByRole(Role.SUPERVISEUR))
                .thenReturn(List.of(superviseurUser));
        doNothing().when(notificationService)
                .creer(any(), any(), anyString(), anyString());

        mockMvc.perform(put("/api/sinistres/1/soumettre-rapport")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("EVALUE"));

        verify(notificationService, atLeast(2))
                .creer(any(), any(), anyString(), anyString());
    }



    @Test
    @WithMockUser(username = "superviseur@test.com", roles = "SUPERVISEUR")
    @DisplayName("PUT /api/sinistres/{id}/approuver – 200 OK quand l'approbation est valide")
    void approuver_shouldReturn200_whenValid() throws Exception {

        Sinistre sinistreApprouve = Sinistre.builder()
                .id(1L).numero("SP-001").statut(StatutSinistre.APPROUVE)
                .client(clientUser).expert(expertUser).build();

        SinistreResponse responseApprouve = SinistreResponse.builder()
                .id(1L).numero("SP-001").statut("APPROUVE").build();

        DecisionRequest request = new DecisionRequest();
        request.setMontantFinal(BigDecimal.valueOf(12000));
        request.setMotif("Dossier complet et valide");

        when(utilisateurRepository.findByEmail("superviseur@test.com"))
                .thenReturn(Optional.of(superviseurUser));
        when(approuverUseCase.approuver(
                eq(1L),
                eq(BigDecimal.valueOf(12000)),
                anyString(),
                eq(4L)))
                .thenReturn(sinistreApprouve);
        when(dtoMapper.toResponse(sinistreApprouve))
                .thenReturn(responseApprouve);
        doNothing().when(notificationService)
                .creer(any(), any(), anyString(), anyString());

        mockMvc.perform(put("/api/sinistres/1/approuver")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("APPROUVE"));

        verify(notificationService, times(2))
                .creer(any(), any(), anyString(), anyString());
    }


    @Test
    @WithMockUser(username = "superviseur@test.com", roles = "SUPERVISEUR")
    @DisplayName("PUT /api/sinistres/{id}/rejeter – 200 OK quand le rejet est valide")
    void rejeter_shouldReturn200_whenValid() throws Exception {

        Sinistre sinistreRejete = Sinistre.builder()
                .id(1L).numero("SP-001").statut(StatutSinistre.REJETE)
                .client(clientUser).expert(expertUser).build();

        SinistreResponse responseRejete = SinistreResponse.builder()
                .id(1L).numero("SP-001").statut("REJETE").build();

        DecisionRequest request = new DecisionRequest();
        request.setMotif("Documents insuffisants");

        when(utilisateurRepository.findByEmail("superviseur@test.com"))
                .thenReturn(Optional.of(superviseurUser));
        when(rejeterUseCase.rejeter(eq(1L), anyString(), eq(4L)))
                .thenReturn(sinistreRejete);
        when(dtoMapper.toResponse(sinistreRejete))
                .thenReturn(responseRejete);
        doNothing().when(notificationService)
                .creer(any(), any(), anyString(), anyString());

        mockMvc.perform(put("/api/sinistres/1/rejeter")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("REJETE"));
    }


    @Test
    @WithMockUser(username = "agent@test.com", roles = "AGENT")
    @DisplayName("PUT /api/sinistres/{id}/cloturer – 200 OK quand la clôture est valide")
    void cloturer_shouldReturn200_whenValid() throws Exception {

        Sinistre sinistreCloture = Sinistre.builder()
                .id(1L).numero("SP-001").statut(StatutSinistre.CLOTURE)
                .client(clientUser).build();

        SinistreResponse responseCloture = SinistreResponse.builder()
                .id(1L).numero("SP-001").statut("CLOTURE").build();

        DecisionRequest request = new DecisionRequest();
        request.setNumeroVirement("VIR-2024-001");
        request.setDatePaiement(LocalDate.of(2024, 7, 1));
        request.setMotif("Paiement effectué par virement bancaire");

        when(cloturerUseCase.cloturer(eq(1L), anyString(), any(LocalDate.class)))
                .thenReturn(sinistreCloture);
        when(dtoMapper.toResponse(sinistreCloture))
                .thenReturn(responseCloture);
        doNothing().when(notificationService)
                .creer(any(), any(), anyString(), anyString());

        mockMvc.perform(put("/api/sinistres/1/cloturer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("CLOTURE"));
    }



    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("GET /api/sinistres/{id} – 200 OK quand le sinistre existe")
    void getById_shouldReturn200_whenFound() throws Exception {

        when(sinistreRepository.findById(1L))
                .thenReturn(Optional.of(sinistreBase));
        when(dtoMapper.toResponse(sinistreBase))
                .thenReturn(sinistreResponseBase);

        mockMvc.perform(get("/api/sinistres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value("SP-001"));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("GET /api/sinistres/{id} – 404 Not Found quand le sinistre n'existe pas")
    void getById_shouldReturn404_whenNotFound() throws Exception {

        when(sinistreRepository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sinistres/99"))
                .andExpect(status().isNotFound());
    }



    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("GET /api/sinistres – 200 OK avec liste complète")
    void getAll_shouldReturn200_withList() throws Exception {

        when(sinistreRepository.findAll())
                .thenReturn(List.of(sinistreBase));
        when(dtoMapper.toResponse(sinistreBase))
                .thenReturn(sinistreResponseBase);

        mockMvc.perform(get("/api/sinistres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].numero").value("SP-001"));
    }


    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("GET /api/sinistres/mes-sinistres – 200 OK pour le client connecté")
    void getMesSinistres_shouldReturn200_forClient() throws Exception {

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(sinistreRepository.findByClientId(1L))
                .thenReturn(List.of(sinistreBase));
        when(dtoMapper.toResponse(sinistreBase))
                .thenReturn(sinistreResponseBase);

        mockMvc.perform(get("/api/sinistres/mes-sinistres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }


    @Test
    @WithMockUser(username = "expert@test.com", roles = "EXPERT")
    @DisplayName("GET /api/sinistres/expert/missions – 200 OK avec les missions de l'expert")
    void getMissionsExpert_shouldReturn200() throws Exception {

        Sinistre mission = Sinistre.builder()
                .id(2L).numero("SP-002").statut(StatutSinistre.AFFECTE)
                .client(clientUser).expert(expertUser).build();

        SinistreResponse missionResponse = SinistreResponse.builder()
                .id(2L).numero("SP-002").statut("AFFECTE").build();

        when(utilisateurRepository.findByEmail("expert@test.com"))
                .thenReturn(Optional.of(expertUser));
        when(sinistreRepository.findByExpertId(3L))
                .thenReturn(List.of(mission));
        when(dtoMapper.toResponse(mission))
                .thenReturn(missionResponse);

        mockMvc.perform(get("/api/sinistres/expert/missions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].statut").value("AFFECTE"));
    }



    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("GET /api/sinistres/stats/resume – 200 OK avec stats CLIENT")
    void getResumeStats_shouldReturn200_forClient() throws Exception {

        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(clientUser));
        when(sinistreRepository.findByClientId(1L))
                .thenReturn(List.of(sinistreBase));

        mockMvc.perform(get("/api/sinistres/stats/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.enCours").value(1))
                .andExpect(jsonPath("$.approuves").value(0))
                .andExpect(jsonPath("$.rejetes").value(0));
    }

    @Test
    @WithMockUser(username = "expert@test.com", roles = "EXPERT")
    @DisplayName("GET /api/sinistres/stats/resume – 200 OK avec stats EXPERT")
    void getResumeStats_shouldReturn200_forExpert() throws Exception {

        when(utilisateurRepository.findByEmail("expert@test.com"))
                .thenReturn(Optional.of(expertUser));
        when(sinistreRepository.findByExpertId(3L))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/sinistres/stats/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.missionsAttente").value(0))
                .andExpect(jsonPath("$.expertisesEnCours").value(0))
                .andExpect(jsonPath("$.dossiersClotures").value(0));
    }
}