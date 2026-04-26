package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.infrastructure.persistence.jpa.DecisionJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.entity.DecisionEntity;
import com.SinistraPro.domain.infrastructure.persistence.mapper.DecisionMapper;
import com.SinistraPro.domain.model.Decision;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecisionRepositoryAdapterTest {

    @Mock
    private DecisionJpaRepository jpaRepository;

    @Mock
    private DecisionMapper mapper;

    @InjectMocks
    private DecisionRepositoryAdapter adapter;

    @Test
    @DisplayName("Succès : Devrait sauvegarder une décision via le mapper et JPA")
    void save_ShouldWorkSuccessfully() {
        // Given
        Decision domainDecision = Decision.builder().id(1L).build();
        DecisionEntity entity = new DecisionEntity();

        when(mapper.toEntity(domainDecision)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domainDecision);

        // When
        Decision result = adapter.save(domainDecision);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(jpaRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Succès : Devrait trouver une décision par son ID")
    void findById_ShouldReturnDecision() {
        // Given
        Long id = 10L;
        DecisionEntity entity = new DecisionEntity();
        Decision domain = Decision.builder().id(id).build();

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Decision> result = adapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Succès : Devrait trouver une décision par l'ID du sinistre")
    void findBySinistreId_ShouldReturnDecision() {
        // Given
        Long sinistreId = 500L;
        DecisionEntity entity = new DecisionEntity();
        Decision domain = Decision.builder().id(1L).build();

        when(jpaRepository.findBySinistreId(sinistreId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Decision> result = adapter.findBySinistreId(sinistreId);

        // Then
        assertThat(result).isPresent();
        verify(jpaRepository).findBySinistreId(sinistreId);
    }

    @Test
    @DisplayName("Vide : Devrait retourner Optional.empty si rien n'est trouvé")
    void findBySinistreId_ShouldReturnEmpty() {
        // Given
        when(jpaRepository.findBySinistreId(any())).thenReturn(Optional.empty());

        // When
        Optional<Decision> result = adapter.findBySinistreId(999L);

        // Then
        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }
}