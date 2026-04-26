package com.SinistraPro.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StatutSinistreTest {

    @Test
    @DisplayName("L'enum devrait contenir le nombre exact de statuts")
    void shouldHaveCorrectNumberOfConstants() {
        assertThat(StatutSinistre.values()).hasSize(7);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "DECLARE",
            "AFFECTE",
            "EN_EXPERTISE",
            "EVALUE",
            "APPROUVE",
            "REJETE",
            "CLOTURE"
    })
    @DisplayName("Chaque valeur nommée devrait exister dans l'Enum")
    void shouldContainAllExpectedValues(String name) {
        assertThat(StatutSinistre.valueOf(name)).isNotNull();
    }

    @Test
    @DisplayName("Les statuts devraient être définis dans un ordre logique de workflow")
    void shouldFollowWorkflowOrder() {
        assertThat(StatutSinistre.DECLARE.ordinal()).isZero();
        assertThat(StatutSinistre.CLOTURE.ordinal()).isEqualTo(6);
    }
}