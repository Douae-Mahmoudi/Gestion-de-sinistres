package com.SinistraPro.domain.application.service;

import com.SinistraPro.domain.model.Utilisateur;
import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UtilisateurRepositoryPort utilisateurRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur();
        utilisateur.setEmail("client@test.com");
        utilisateur.setNom("Doe");
        utilisateur.setPrenom("John");
    }



    @Test
    void demanderReset_emailExistant_envoieEmail() {
        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(utilisateur));

        passwordResetService.demanderReset("client@test.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).contains("client@test.com");
        assertThat(message.getSubject()).contains("SinistraPro");
        assertThat(message.getText()).contains("15 minutes");
    }

    @Test
    void demanderReset_emailInexistant_leveException() {
        when(utilisateurRepository.findByEmail("inconnu@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.demanderReset("inconnu@test.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Aucun compte associé à cet email");
    }



    @Test
    void verifierCode_codeCorrect_neLeveAucuneException() {
        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(utilisateur));
        passwordResetService.demanderReset("client@test.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        String codeEnvoye = extraireCode(captor.getValue().getText());

        assertThatCode(() -> passwordResetService.verifierCode("client@test.com", codeEnvoye))
                .doesNotThrowAnyException();
    }

    @Test
    void verifierCode_codeIncorrect_leveException() {
        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(utilisateur));
        passwordResetService.demanderReset("client@test.com");

        assertThatThrownBy(() -> passwordResetService.verifierCode("client@test.com", "000000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Code incorrect");
    }

    @Test
    void verifierCode_aucuneDemandeExistante_leveException() {
        assertThatThrownBy(() -> passwordResetService.verifierCode("sansDemande@test.com", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Aucune demande de réinitialisation");
    }



    @Test
    void reinitialiserMotDePasse_codeValide_metAJourMotDePasse() {
        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(utilisateur));
        passwordResetService.demanderReset("client@test.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        String codeEnvoye = extraireCode(captor.getValue().getText());

        passwordResetService.reinitialiserMotDePasse("client@test.com", codeEnvoye, "nouveauMotDePasse123");

        verify(utilisateurRepository, times(1))
                .updatePassword("client@test.com", "nouveauMotDePasse123");
    }

    @Test
    void reinitialiserMotDePasse_codeInvalide_leveException() {
        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(utilisateur));
        passwordResetService.demanderReset("client@test.com");

        assertThatThrownBy(() -> passwordResetService.reinitialiserMotDePasse(
                "client@test.com", "mauvaisCode", "nouveauMotDePasse123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Code incorrect");
    }

    @Test
    void reinitialiserMotDePasse_apresReset_codeNePeutPlusEtreUtilise() {
        when(utilisateurRepository.findByEmail("client@test.com"))
                .thenReturn(Optional.of(utilisateur));
        passwordResetService.demanderReset("client@test.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        String codeEnvoye = extraireCode(captor.getValue().getText());

        passwordResetService.reinitialiserMotDePasse("client@test.com", codeEnvoye, "nouveauMotDePasse123");

        assertThatThrownBy(() -> passwordResetService.verifierCode("client@test.com", codeEnvoye))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Aucune demande de réinitialisation");
    }


    private String extraireCode(String texteEmail) {
        String[] lignes = texteEmail.split("\n");
        for (int i = 0; i < lignes.length; i++) {
            if (lignes[i].contains("━━━━━━━━━━━━") && i + 1 < lignes.length) {
                String candidat = lignes[i + 1].trim();
                if (candidat.matches("\\d{6}")) {
                    return candidat;
                }
            }
        }
        throw new RuntimeException("Code introuvable dans le mail");
    }
}

















