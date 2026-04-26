package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    @DisplayName("L'enum Role devrait contenir exactement 4 rôles")
    void shouldHaveFourRoles() {
        assertThat(Role.values()).hasSize(4);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CLIENT", "AGENT", "EXPERT", "SUPERVISEUR"})
    @DisplayName("Chaque nom de rôle défini doit être valide")
    void shouldCheckRoleNames(String roleName) {
        assertThat(Role.valueOf(roleName)).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(Role.class)
    @DisplayName("Chaque rôle doit être transformable en String correctement")
    void shouldVerifyToStringMatchesName(Role role) {
        assertThat(role.toString()).isEqualTo(role.name());
    }

    @Test
    @DisplayName("Vérification spécifique des valeurs pour la sécurité")
    void shouldVerifySpecificConstants() {
        assertThat(Role.CLIENT).isNotNull();
        assertThat(Role.AGENT).isNotNull();
        assertThat(Role.EXPERT).isNotNull();
        assertThat(Role.SUPERVISEUR).isNotNull();
    }
}