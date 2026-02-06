package com.yowyob.template.infrastructure.config;

import com.yowyob.template.infrastructure.security.CustomOpaqueTokenIntrospector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, CustomOpaqueTokenIntrospector introspector) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                "/api/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/register-init",
                                "/api/v1/auth/verify-otp",
                                "/api-docs/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/client/annonces", "/api/v1/driver/plannings", "/api/v1/driver/plannings/user/*","/api/v1/driver/annonces/user/*" ,"/vehicles/*", "/vehicles/*/images", "/vehicles/user/*", "/api/v1/reviews/user/*", "/api/v1/reviews","/api/v1/reviews/*", "/api/v1/reactions", "/api/v1/reactions/*", "/api/v1/reactions/user/*", "/api/v1/client/profile/user/*", "/api/v1/client/profile/addresses/user/*", "/api/v1/driver/profile/user/*", "/api/v1/driver/profile/addresses/user/*", "/api/v1/users/*")
                        .permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(opaque -> opaque.introspector(introspector)))
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Allow all origins
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
