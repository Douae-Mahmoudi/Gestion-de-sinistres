package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StatutDecisionTest {

    @Test
    @DisplayName("L'enum StatutDecision doit contenir exactement 2 options")
    void shouldHaveTwoValues() {
        assertThat(StatutDecision.values()).hasSize(2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"APPROUVE", "REJETE"})
    @DisplayName("Les constantes APPROUVE et REJETE doivent être présentes")
    void shouldContainExpectedConstants(String name) {
        assertThat(StatutDecision.valueOf(name)).isNotNull();
    }

    @Test
    @DisplayName("Vérifie que les noms sont corrects pour la sérialisation")
    void shouldVerifyNames() {
        assertThat(StatutDecision.APPROUVE.name()).isEqualTo("APPROUVE");
        assertThat(StatutDecision.REJETE.name()).isEqualTo("REJETE");
    }
}