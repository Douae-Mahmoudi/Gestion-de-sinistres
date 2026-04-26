package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.StatutSinistre;
import com.SinistraPro.domain.port.out.SinistreRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
public class StatsControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private SinistreRepositoryPort sinistreRepository;

    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;
    @MockBean private UtilisateurRepositoryPort utilisateurRepository;
    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    void shouldReturnStatsByStatut() throws Exception {
        when(sinistreRepository.findByStatut(StatutSinistre.EVALUE))
                .thenReturn(List.of(new Sinistre(), new Sinistre()));
        when(sinistreRepository.findByStatut(StatutSinistre.APPROUVE))
                .thenReturn(List.of(new Sinistre()));
        when(sinistreRepository.findByStatut(StatutSinistre.REJETE))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/stats/sinistres-par-statut")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.EVALUE").value(2))
                .andExpect(jsonPath("$.APPROUVE").value(1))
                .andExpect(jsonPath("$.REJETE").value(0));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    void shouldReturnGlobalResume() throws Exception {
        List<Sinistre> allSinistres = List.of(new Sinistre(), new Sinistre(), new Sinistre(), new Sinistre());

        when(sinistreRepository.findAll()).thenReturn(allSinistres);
        when(sinistreRepository.findByStatut(StatutSinistre.APPROUVE)).thenReturn(List.of(new Sinistre()));
        when(sinistreRepository.findByStatut(StatutSinistre.REJETE)).thenReturn(List.of(new Sinistre()));
        when(sinistreRepository.findByStatut(StatutSinistre.CLOTURE)).thenReturn(List.of());

        mockMvc.perform(get("/api/stats/resume")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(4))
                .andExpect(jsonPath("$.approuves").value(1))
                .andExpect(jsonPath("$.rejetes").value(1))
                .andExpect(jsonPath("$.clotures").value(0))
                .andExpect(jsonPath("$.enCours").value(2));
    }
}