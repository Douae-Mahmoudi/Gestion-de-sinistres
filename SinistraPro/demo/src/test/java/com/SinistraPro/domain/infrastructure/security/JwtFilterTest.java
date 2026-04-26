package com.SinistraPro.domain.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock private JwtService jwtService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_sansHeader_continuesSansAuthentifier() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_headerSansBearer_continuesSansAuthentifier() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).isTokenValid(any());
    }

    @Test
    void doFilterInternal_tokenInvalide_continuesSansAuthentifier() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenInvalide");
        when(jwtService.isTokenValid("tokenInvalide")).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(jwtService, never()).extractEmail(any());
    }

    @Test
    void doFilterInternal_tokenValideClient_authentifieAvecRoleROLE_CLIENT() throws Exception {
        String token = "validToken.client.jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn("client@test.com");
        when(jwtService.extractRole(token)).thenReturn("CLIENT");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo("client@test.com");
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_CLIENT");
    }

    @Test
    void doFilterInternal_tokenValideAgent_authentifieAvecRoleROLE_AGENT() throws Exception {
        String token = "validToken.agent.jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn("agent@test.com");
        when(jwtService.extractRole(token)).thenReturn("AGENT");

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo("agent@test.com");
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_AGENT");
    }

    @Test
    void doFilterInternal_tokenValideExpert_authentifieAvecRoleROLE_EXPERT() throws Exception {
        String token = "validToken.expert.jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn("expert@test.com");
        when(jwtService.extractRole(token)).thenReturn("EXPERT");

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_EXPERT");
    }

    @Test
    void doFilterInternal_tokenValideSuperviseur_authentifieAvecRoleROLE_SUPERVISEUR() throws Exception {
        String token = "validToken.superviseur.jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn("superviseur@test.com");
        when(jwtService.extractRole(token)).thenReturn("SUPERVISEUR");

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_SUPERVISEUR");
    }

    @Test
    void doFilterInternal_tokenValide_credentialsEstNull() throws Exception {
        String token = "validToken.jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn("user@test.com");
        when(jwtService.extractRole(token)).thenReturn("CLIENT");

        jwtFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth.getCredentials()).isNull();
    }

    @Test
    void doFilterInternal_tokenValide_filterChainEstToujoursAppele() throws Exception {
        String token = "validToken.jwt";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.isTokenValid(token)).thenReturn(true);
        when(jwtService.extractEmail(token)).thenReturn("user@test.com");
        when(jwtService.extractRole(token)).thenReturn("CLIENT");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}