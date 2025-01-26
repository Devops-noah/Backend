package fr.parisnanterre.noah.config;

import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Service.CustomUserDetails;
import fr.parisnanterre.noah.Service.CustomUserDetailsService;
import fr.parisnanterre.noah.util.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:3000"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Requested-With"));
                    corsConfig.setExposedHeaders(List.of("Authorization"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/annonces/filter").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/notations/last-three-approved").permitAll()
                        .requestMatchers("/api/notations/approved").permitAll()
                        .requestMatchers("/api/annonces").permitAll() // Allow public acc
                        // ess to `getAllAnnonces`
                        .requestMatchers("/api/demandeTransfert/recherche").permitAll()
                        .requestMatchers("/uploads/**").permitAll()  // Allow public access to uploaded files
                        .requestMatchers("/api/utilisateurs/profiles/images/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/me").hasAnyRole("Voyageur", "Expediteur")
                        .requestMatchers("/api/annonces/**").authenticated()
                        .requestMatchers("/api/notations").authenticated()
                        .requestMatchers("/api/pays").authenticated()
                        .requestMatchers("/api/pays/**").authenticated()
                        .requestMatchers("/api/voyages").authenticated()
                        .requestMatchers("/api/voyages/**").authenticated()
                        .requestMatchers("/api/utilisateurs/profile").authenticated()
                        .requestMatchers("/api/utilisateurs/profile/update/**").authenticated()
                        .requestMatchers("/api/utilisateurs/profile/upload-image").authenticated()
                        .requestMatchers("/api/utilisateurs/profile/update-image").authenticated()
                        .requestMatchers("/api/information_colis/**").authenticated() // Autorisation pour les expéditeurs
                        .requestMatchers("/api/notifications/unread").authenticated()
                        .requestMatchers("/api/notifications/read/**").authenticated()
                        .requestMatchers("/api/demandes/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String message = "Access Denied";

                            if (authException instanceof DisabledException) {
                                message = authException.getMessage(); // "User account is disabled"
                            }

                            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // HTTP 403
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"" + message + "\"}");
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        // Add a UserDetailsChecker to validate the account status
        authProvider.setPreAuthenticationChecks(userDetails -> {
            if (userDetails instanceof CustomUserDetails) {
                Utilisateur utilisateur = ((CustomUserDetails) userDetails).getUtilisateur();
                if (!utilisateur.isEnabled()) {
                    throw new DisabledException("User account is disabled");
                }
            } else {
                throw new ClassCastException("Expected CustomUserDetails but got " + userDetails.getClass());
            }
        });

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
