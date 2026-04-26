package com.SinistraPro.domain.infrastructure.persistence.adapter;

import com.SinistraPro.domain.infrastructure.persistence.entity.UtilisateurEntity;
import com.SinistraPro.domain.infrastructure.persistence.jpa.UtilisateurJpaRepository;
import com.SinistraPro.domain.infrastructure.persistence.mapper.UtilisateurMapper;
import com.SinistraPro.domain.model.Role;
import com.SinistraPro.domain.model.Utilisateur;
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
class UtilisateurRepositoryAdapterTest {

    @Mock
    private UtilisateurJpaRepository jpaRepository;

    @Mock
    private UtilisateurMapper mapper;

    @InjectMocks
    private UtilisateurRepositoryAdapter adapter;

    @Test
    @DisplayName("Succès : Recherche d'un utilisateur par email")
    void findByEmail_ShouldReturnUser() {
        // Given
        String email = "expert@sinistra.pro";
        UtilisateurEntity entity = new UtilisateurEntity();
        Utilisateur domain = Utilisateur.builder().email(email).build();

        when(jpaRepository.findByEmail(email)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        // When
        Optional<Utilisateur> result = adapter.findByEmail(email);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
        verify(jpaRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Succès : Recherche par Reset Token")
    void findByResetToken_ShouldReturnUser() {
        // Given
        String token = "reset-123";
        UtilisateurEntity entity = new UtilisateurEntity();
        when(jpaRepository.findByResetToken(token)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(Utilisateur.builder().resetToken(token).build());

        // When
        Optional<Utilisateur> result = adapter.findByResetToken(token);

        // Then
        assertThat(result).isPresent();
        verify(jpaRepository).findByResetToken(token);
    }

    @Test
    @DisplayName("Succès : Recherche des utilisateurs par rôle")
    void findByRole_ShouldReturnList() {
        // Given
        Role role = Role.EXPERT;
        List<UtilisateurEntity> entities = List.of(new UtilisateurEntity(), new UtilisateurEntity());
        when(jpaRepository.findByRole(role)).thenReturn(entities);
        when(mapper.toDomain(any())).thenReturn(Utilisateur.builder().role(role).build());

        // When
        List<Utilisateur> result = adapter.findByRole(role);

        // Then
        assertThat(result).hasSize(2);
        verify(jpaRepository).findByRole(role);
    }

    @Test
    @DisplayName("Succès : Vérifier si l'email existe déjà")
    void existsByEmail_ShouldReturnBoolean() {
        // Given
        when(jpaRepository.existsByEmail("test@test.com")).thenReturn(true);

        // When
        boolean exists = adapter.existsByEmail("test@test.com");

        // Then
        assertThat(exists).isTrue();
    }
}