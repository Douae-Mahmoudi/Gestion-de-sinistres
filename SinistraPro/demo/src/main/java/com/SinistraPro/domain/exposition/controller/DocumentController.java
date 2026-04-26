package com.SinistraPro.domain.exposition.controller;

import com.SinistraPro.domain.model.Document;
import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.DocumentRepositoryPort;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import com.SinistraPro.domain.exposition.dto.response.DocumentResponse;
import com.SinistraPro.domain.exposition.mapper.SinistreDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private static final String UPLOAD_DIR = "uploads/";

    private final DocumentRepositoryPort documentRepository;
    private final UtilisateurRepositoryPort utilisateurRepository;
    private final SinistreDtoMapper dtoMapper;

    @PostMapping("/upload/{sinistreId}")
    public ResponseEntity<DocumentResponse> upload(
            @PathVariable Long sinistreId,
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("typeDocument") String typeDocument,
            Authentication auth) throws IOException {

        Utilisateur uploader = utilisateurRepository
                .findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        // Sauvegarder le fichier sur disque
        String nomFichier = UUID.randomUUID() + "_" + fichier.getOriginalFilename();
        Path chemin = Paths.get(UPLOAD_DIR + nomFichier);
        Files.createDirectories(chemin.getParent());
        Files.write(chemin, fichier.getBytes());

        Document document = Document.builder()
                .nomFichier(fichier.getOriginalFilename())
                .cheminFichier(chemin.toString())
                .typeDocument(typeDocument)
                .taille(fichier.getSize())
                .dateUpload(LocalDateTime.now())
                .uploadePar(uploader)
                .build();

        Document saved = documentRepository.save(document, sinistreId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dtoMapper.toDocumentResponse(saved));
    }

    @GetMapping("/sinistre/{sinistreId}")
    public ResponseEntity<List<DocumentResponse>> getBySinistre(
            @PathVariable Long sinistreId) {

        List<DocumentResponse> list = documentRepository
                .findBySinistreId(sinistreId).stream()
                .map(dtoMapper::toDocumentResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> download(
            @PathVariable Long id) throws IOException {

        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable"));

        Path chemin = Paths.get(document.getCheminFichier());
        byte[] data = Files.readAllBytes(chemin);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getNomFichier() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(data));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}