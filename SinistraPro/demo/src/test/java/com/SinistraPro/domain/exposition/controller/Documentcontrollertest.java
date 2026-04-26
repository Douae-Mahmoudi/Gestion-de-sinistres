package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.exposition.dto.response.DocumentResponse;
import com.SinistraPro.domain.exposition.dto.response.UtilisateurResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import com.SinistraPro.domain.infrastructure.security.JwtService;
import com.SinistraPro.domain.model.Document;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.DocumentRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private DocumentRepositoryPort documentRepository;

    @MockBean
    private UtilisateurRepositoryPort utilisateurRepository;

    @MockBean
    private SinistreDtoMapper dtoMapper;

    @Test
    @DisplayName("Succès : Upload d'un document")
    void upload_ShouldReturnCreated() throws Exception {
        String userEmail = "user@test.com";

        Authentication auth = new UsernamePasswordAuthenticationToken(userEmail, null, Collections.emptyList());

        MockMultipartFile file = new MockMultipartFile(
                "fichier", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "content".getBytes());

        Utilisateur uploader = Utilisateur.builder().email(userEmail).build();
        Document savedDoc = Document.builder().id(1L).build();
        DocumentResponse response = DocumentResponse.builder()
                .id(1L)
                .nomFichier("test.pdf")
                .uploadePar(UtilisateurResponse.builder().email(userEmail).build())
                .build();

        when(utilisateurRepository.findByEmail(userEmail)).thenReturn(Optional.of(uploader));
        when(documentRepository.save(any(), eq(1L))).thenReturn(savedDoc);
        when(dtoMapper.toDocumentResponse(savedDoc)).thenReturn(response);

        mockMvc.perform(multipart("/api/documents/upload/1")
                        .file(file)
                        .param("typeDocument", "CONSTAT")

                        .principal(auth))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomFichier").value("test.pdf"));
    }

    @Test
    @DisplayName("Succès : Lister les documents")
    void getBySinistre_ShouldReturnList() throws Exception {
        when(documentRepository.findBySinistreId(1L)).thenReturn(List.of(new Document()));
        when(dtoMapper.toDocumentResponse(any())).thenReturn(DocumentResponse.builder().id(1L).build());

        mockMvc.perform(get("/api/documents/sinistre/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}