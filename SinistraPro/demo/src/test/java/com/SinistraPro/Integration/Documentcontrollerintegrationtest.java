package com.SinistraPro.Integration;

import com.SinistraPro.domain.exposition.controller.DocumentController;
import com.SinistraPro.domain.exposition.dto.response.DocumentResponse;
import com.SinistraPro.domain.exposition.exception.GlobalExceptionHandler;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.Document;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.DocumentRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({DocumentController.class, GlobalExceptionHandler.class})
@DisplayName("Tests d'intégration – DocumentController")
class DocumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private DocumentRepositoryPort documentRepository;
    @MockBean private UtilisateurRepositoryPort utilisateurRepository;
    @MockBean private SinistreDtoMapper dtoMapper;

    private Utilisateur clientUser;
    private Document documentBase;
    private DocumentResponse documentResponse;

    @BeforeEach
    void setUp() {
        clientUser = Utilisateur.builder()
                .id(1L).nom("Dupont").prenom("Jean")
                .email("client@test.com").role(Role.CLIENT).build();

        documentBase = Document.builder()
                .id(1L)
                .nomFichier("constat.pdf")
                .typeDocument("CONSTAT")
                .build();

        documentResponse = DocumentResponse.builder()
                .id(1L)
                .nomFichier("constat.pdf")
                .typeDocument("CONSTAT")
                .build();
    }

    @Test
    @WithMockUser(username = "client@test.com", roles = "CLIENT")
    @DisplayName("POST /upload – 201 Created")
    void upload_shouldReturn201_whenFileIsValid() throws Exception {
        MockMultipartFile fichier = new MockMultipartFile("fichier", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "data".getBytes());

        when(utilisateurRepository.findByEmail("client@test.com")).thenReturn(Optional.of(clientUser));
        when(documentRepository.save(any(Document.class), eq(1L))).thenReturn(documentBase);
        when(dtoMapper.toDocumentResponse(any())).thenReturn(documentResponse);

        mockMvc.perform(multipart("/api/documents/upload/1")
                        .file(fichier)
                        .param("typeDocument", "CONSTAT")
                        .with(csrf()))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("GET /download – 404 Not Found (Corrigé)")
    void download_shouldReturn404_whenNotFound() throws Exception {
        // Simulation de l'absence du document
        when(documentRepository.findById(99L)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/documents/99/download"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("DELETE /{id} – 204 No Content")
    void delete_shouldReturn204_whenDeleted() throws Exception {
        doNothing().when(documentRepository).deleteById(1L);

        mockMvc.perform(delete("/api/documents/1").with(csrf()))
                .andExpect(status().isNoContent());
    }
}