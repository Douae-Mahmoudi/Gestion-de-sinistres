package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.infrastructure.persistence.entity.HistoriqueEntity;
import com.SinistraPro.domain.infrastructure.persistence.entity.SinistreEntity;
import com.SinistraPro.domain.infrastructure.persistence.jpa.HistoriqueJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.jpa.SinistreJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.HistoriqueMapper;
import com.SinistraPro.domain.model.Historique;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoriqueRepositoryAdapterTest {

    @Mock
    private HistoriqueJpaRepository jpaRepository;

    @Mock
    private SinistreJpaRepository sinistreJpaRepository;

    @Mock
    private HistoriqueMapper mapper;

    @InjectMocks
    private HistoriqueRepositoryAdapter adapter;

    @Test
    @DisplayName("Succès : Devrait sauvegarder l'historique lié à un sinistre existant")
    void save_ShouldWorkSuccessfully() {
        // Given
        Long sinistreId = 100L;
        Historique domainHistorique = Historique.builder()
                .sinistreId(sinistreId)
                .nouveauStatut(StatutSinistre.AFFECTE)
                .build();

        SinistreEntity sinistreEntity = new SinistreEntity();
        HistoriqueEntity historiqueEntity = new HistoriqueEntity();

        when(sinistreJpaRepository.findById(sinistreId)).thenReturn(Optional.of(sinistreEntity));
        when(mapper.toEntity(domainHistorique, sinistreEntity)).thenReturn(historiqueEntity);
        when(jpaRepository.save(historiqueEntity)).thenReturn(historiqueEntity);
        when(mapper.toDomain(historiqueEntity)).thenReturn(domainHistorique);

        // When
        Historique result = adapter.save(domainHistorique);

        // Then
        assertThat(result).isNotNull();
        verify(sinistreJpaRepository).findById(sinistreId);
        verify(jpaRepository).save(historiqueEntity);
    }

    @Test
    @DisplayName("Échec : Devrait lever une exception si le sinistre est introuvable")
    void save_ShouldThrowExceptionWhenSinistreNotFound() {
        // Given
        Historique domainHistorique = Historique.builder().sinistreId(999L).build();
        when(sinistreJpaRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adapter.save(domainHistorique))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sinistre introuvable id=999");

        verify(jpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Succès : Devrait lister l'historique par ordre chronologique")
    void findBySinistreId_ShouldReturnOrderedList() {
        // Given
        Long sId = 1L;
        List<HistoriqueEntity> entities = List.of(new HistoriqueEntity(), new HistoriqueEntity());
        when(jpaRepository.findBySinistreIdOrderByDateActionAsc(sId)).thenReturn(entities);
        when(mapper.toDomain(any())).thenReturn(Historique.builder().build());

        // When
        List<Historique> result = adapter.findBySinistreId(sId);

        // Then
        assertThat(result).hasSize(2);
        verify(jpaRepository).findBySinistreIdOrderByDateActionAsc(sId);
        verify(mapper, times(2)).toDomain(any());
    }
}