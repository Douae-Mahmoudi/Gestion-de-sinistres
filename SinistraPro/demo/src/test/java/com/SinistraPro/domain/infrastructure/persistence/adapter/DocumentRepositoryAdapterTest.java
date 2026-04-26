package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.infrastructure.persistence.entity.DocumentEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.jpa.DocumentJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.jpa.SinistreJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.DocumentMapper;
import com.SinistraPro.domain.model.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentRepositoryAdapterTest {

    @Mock
    private DocumentJpaRepository jpaRepository;

    @Mock
    private SinistreJpaRepository sinistreJpaRepository;

    @Mock
    private DocumentMapper mapper;

    @InjectMocks
    private DocumentRepositoryAdapter adapter;

    @Test
    @DisplayName("Succès : Devrait sauvegarder un document lié à un sinistre existant")
    void save_ShouldWorkWhenSinistreExists() {
        // Given
        Long sinistreId = 1L;
        Document domainDoc = Document.builder().nomFichier("constat.pdf").build();
        SinistreEntity sinistreEntity = new SinistreEntity();
        DocumentEntity documentEntity = new DocumentEntity();

        when(sinistreJpaRepository.findById(sinistreId)).thenReturn(Optional.of(sinistreEntity));
        when(mapper.toEntity(domainDoc, sinistreEntity)).thenReturn(documentEntity);
        when(jpaRepository.save(documentEntity)).thenReturn(documentEntity);
        when(mapper.toDomain(documentEntity)).thenReturn(domainDoc);

        // When
        Document result = adapter.save(domainDoc, sinistreId);

        // Then
        assertThat(result).isNotNull();
        verify(sinistreJpaRepository).findById(sinistreId);
        verify(jpaRepository).save(documentEntity);
    }

    @Test
    @DisplayName("Échec : Devrait lever une exception si le sinistre est introuvable lors de l'upload")
    void save_ShouldThrowExceptionWhenSinistreNotFound() {
        // Given
        when(sinistreJpaRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adapter.save(new Document(), 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sinistre introuvable");

        verify(jpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Succès : Devrait lister tous les documents d'un sinistre")
    void findBySinistreId_ShouldReturnList() {
        // Given
        Long sId = 1L;
        List<DocumentEntity> entities = List.of(new DocumentEntity(), new DocumentEntity());
        when(jpaRepository.findBySinistreId(sId)).thenReturn(entities);
        when(mapper.toDomain(any())).thenReturn(new Document());

        // When
        List<Document> result = adapter.findBySinistreId(sId);

        // Then
        assertThat(result).hasSize(2);
        verify(jpaRepository).findBySinistreId(sId);
        verify(mapper, times(2)).toDomain(any());
    }

    @Test
    @DisplayName("Succès : Devrait appeler le delete du JpaRepository")
    void deleteById_ShouldCallJpa() {
        // Given
        Long docId = 10L;

        // When
        adapter.deleteById(docId);

        // Then
        verify(jpaRepository).deleteById(docId);
    }
}