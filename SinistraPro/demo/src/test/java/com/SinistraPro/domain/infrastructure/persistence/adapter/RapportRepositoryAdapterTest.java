package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.infrastructure.persistence.entity.RapportEntity;
import com.SinistraPro.domain.infrastructure.persistence.jpa.RapportJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.RapportMapper;
import com.SinistraPro.domain.model.Rapport;
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
class RapportRepositoryAdapterTest {

    @Mock
    private RapportJpaRepository jpaRepository;

    @Mock
    private RapportMapper mapper;

    @InjectMocks
    private RapportRepositoryAdapter adapter;

    @Test
    @DisplayName("Succès : Devrait sauvegarder un rapport d'expertise")
    void save_ShouldWorkSuccessfully() {
        // Given
        Rapport domainRapport = Rapport.builder().id(1L).descriptionDommages("Pare-brise fissuré").build();
        RapportEntity entity = new RapportEntity();

        when(mapper.toEntity(domainRapport)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domainRapport);

        // When
        Rapport result = adapter.save(domainRapport);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescriptionDommages()).isEqualTo("Pare-brise fissuré");
        verify(jpaRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Succès : Devrait trouver un rapport par l'ID du sinistre")
    void findBySinistreId_ShouldReturnRapport() {
        // Given
        Long sinistreId = 123L;
        RapportEntity entity = new RapportEntity();
        Rapport domain = Rapport.builder().id(1L).build();

        when(jpaRepository.findBySinistreId(sinistreId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Rapport> result = adapter.findBySinistreId(sinistreId);

        // Then
        assertThat(result).isPresent();
        verify(jpaRepository).findBySinistreId(sinistreId);
    }

    @Test
    @DisplayName("Vide : Devrait retourner Optional.empty si aucun rapport pour ce sinistre")
    void findBySinistreId_ShouldReturnEmpty() {
        // Given
        when(jpaRepository.findBySinistreId(any())).thenReturn(Optional.empty());

        // When
        Optional<Rapport> result = adapter.findBySinistreId(999L);

        // Then
        assertThat(result).isEmpty();
        verify(mapper, never()).toDomain(any());
    }
}