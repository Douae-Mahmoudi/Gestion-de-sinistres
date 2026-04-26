package com.SinistraPro.domain.infrastructure.security;

import com.SinistraPro.domain.port.out.UtilisateurRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UtilisateurRepositoryPort utilisateurRepository;

    private static final String[] ALL_ROLES = {
            "ROLE_CLIENT", "ROLE_AGENT", "ROLE_EXPERT", "ROLE_SUPERVISEUR"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/rapports/**")
                        .hasAnyAuthority(ALL_ROLES)

                        .requestMatchers(HttpMethod.GET, "/api/decisions/**")
                        .hasAnyAuthority(ALL_ROLES)

                        .requestMatchers("/api/documents/**")
                        .hasAnyAuthority(ALL_ROLES)

                        .requestMatchers("/api/notifications/**")
                        .authenticated()

                        .requestMatchers("/api/utilisateurs/me/**")
                        .authenticated()
                        .requestMatchers("/api/utilisateurs/experts")
                        .hasAnyAuthority("ROLE_AGENT", "ROLE_SUPERVISEUR")

                        .requestMatchers("/api/stats/**")
                        .hasAnyAuthority("ROLE_AGENT", "ROLE_SUPERVISEUR")


                        // CLIENT
                        .requestMatchers(HttpMethod.POST, "/api/sinistres")
                        .hasAuthority("ROLE_CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/sinistres/mes-sinistres")
                        .hasAuthority("ROLE_CLIENT")

                        // EXPERT
                        .requestMatchers(HttpMethod.GET, "/api/sinistres/expert/missions")
                        .hasAuthority("ROLE_EXPERT")
                        .requestMatchers(HttpMethod.GET, "/api/sinistres/stats/resume")
                        .hasAnyAuthority("ROLE_CLIENT", "ROLE_EXPERT")
                        .requestMatchers(HttpMethod.PUT, "/api/sinistres/{id}/soumettre-rapport")
                        .hasAuthority("ROLE_EXPERT")

                        // AGENT
                        .requestMatchers(HttpMethod.PUT, "/api/sinistres/{id}/affecter")
                        .hasAuthority("ROLE_AGENT")
                        .requestMatchers(HttpMethod.PUT, "/api/sinistres/{id}/cloturer")
                        .hasAuthority("ROLE_AGENT")
                        .requestMatchers(HttpMethod.GET, "/api/sinistres")
                        .hasAnyAuthority("ROLE_AGENT", "ROLE_SUPERVISEUR")

                        // SUPERVISEUR
                        .requestMatchers(HttpMethod.PUT, "/api/sinistres/{id}/approuver")
                        .hasAuthority("ROLE_SUPERVISEUR")
                        .requestMatchers(HttpMethod.PUT, "/api/sinistres/{id}/rejeter")
                        .hasAuthority("ROLE_SUPERVISEUR")

                        .requestMatchers(HttpMethod.GET, "/api/sinistres/*")
                        .hasAnyAuthority(ALL_ROLES)

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8100"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            var utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + email));
            String roleName = utilisateur.getRole().name().toUpperCase();
            if (!roleName.startsWith("ROLE_")) roleName = "ROLE_" + roleName;
            return org.springframework.security.core.userdetails.User
                    .withUsername(utilisateur.getEmail())
                    .password(utilisateur.getMotDePasse())
                    .authorities(roleName)
                    .build();
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}