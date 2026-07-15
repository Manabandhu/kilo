package com.manabandhu.backend.security;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthFilter authFilter;
    private final List<String> allowedOrigins;

    public SecurityConfig(AuthFilter authFilter,
                          @org.springframework.beans.factory.annotation.Value("${cors.allowed-origins:}") String origins) {
        this.authFilter = authFilter;
        this.allowedOrigins = origins.isBlank() ? List.of() : List.of(origins.split(","));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/swagger-ui/**",
                                "/v3/api-docs/**", "/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/moderation/**").hasAnyAuthority("ROLE_MODERATOR", "ROLE_ADMIN")
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-Correlation-Id"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
