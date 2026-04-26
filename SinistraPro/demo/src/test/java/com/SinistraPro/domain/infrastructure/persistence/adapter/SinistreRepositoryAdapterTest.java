package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.jpa.SinistreJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.SinistreMapper;
import com.SinistraPro.domain.model.Sinistre;
import com.SinistraPro.domain.model.StatutSinistre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SinistreRepositoryAdapterTest {

    @Mock
    private SinistreJpaRepository jpaRepository;

    @Mock
    private SinistreMapper mapper;

    @InjectMocks
    private SinistreRepositoryAdapter adapter;

    @Test
    @DisplayName("Succès : Sauvegarde d'un sinistre")
    void save_ShouldWorkSuccessfully() {
        // Given
        Sinistre domain = Sinistre.builder().id(1L).build();
        SinistreEntity entity = new SinistreEntity();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Sinistre result = adapter.save(domain);

        // Then
        assertThat(result).isNotNull();
        verify(jpaRepository).save(entity);
    }

    @Test
    @DisplayName("Succès : Recherche par ID")
    void findById_ShouldReturnOptional() {
        // Given
        Long id = 10L;
        SinistreEntity entity = new SinistreEntity();
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(Sinistre.builder().id(id).build());

        // When
        Optional<Sinistre> result = adapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Succès : Recherche par Statut")
    void findByStatut_ShouldReturnList() {
        // Given
        StatutSinistre statut = StatutSinistre.DECLARE;
        List<SinistreEntity> entities = List.of(new SinistreEntity(), new SinistreEntity());
        when(jpaRepository.findByStatut(statut)).thenReturn(entities);
        when(mapper.toDomain(any())).thenReturn(Sinistre.builder().build());

        // When
        List<Sinistre> result = adapter.findByStatut(statut);

        // Then
        assertThat(result).hasSize(2);
        verify(jpaRepository).findByStatut(statut);
    }

    @Test
    @DisplayName("Succès : Recherche par Expert")
    void findByExpertId_ShouldReturnList() {
        // Given
        Long expertId = 5L;
        when(jpaRepository.findByExpertId(expertId)).thenReturn(List.of(new SinistreEntity()));
        when(mapper.toDomain(any())).thenReturn(Sinistre.builder().build());

        // When
        List<Sinistre> result = adapter.findByExpertId(expertId);

        // Then
        assertThat(result).isNotEmpty();
        verify(jpaRepository).findByExpertId(expertId);
    }

    @Test
    @DisplayName("Vérification : existsById")
    void existsById_ShouldCallJpa() {
        // Given
        when(jpaRepository.existsById(1L)).thenReturn(true);

        // When
        boolean exists = adapter.existsById(1L);

        // Then
        assertThat(exists).isTrue();
        verify(jpaRepository).existsById(1L);
    }
}