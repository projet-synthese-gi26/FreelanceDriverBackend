package com.yowyob.template.infrastructure.security;

import com.yowyob.template.infrastructure.adapters.outbound.external.dto.TraMaSysUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomOpaqueTokenIntrospector implements ReactiveOpaqueTokenIntrospector {

    private final WebClient.Builder webClientBuilder;

    @Value("${application.external.auth-service-url}")
    private String authServiceUrl;

    @Override
    public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
        return webClientBuilder.baseUrl(authServiceUrl).build()
                .get()
                .uri("/api/auth/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(TraMaSysUserResponse.class)
                .map(user -> {
                    Map<String, Object> attributes = new HashMap<>();
                    attributes.put("sub", user.id());
                    attributes.put("username", user.username());
                    attributes.put("email", user.email());
                    attributes.put("firstName", user.firstName());
                    attributes.put("lastName", user.lastName());
                    // Marking as active for compatibility if needed by some components, 
                    // though DefaultOAuth2AuthenticatedPrincipal doesn't strictly require it unless used with Introspection validators.
                    attributes.put("active", true); 

                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (user.roles() != null) {
                        authorities.addAll(user.roles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList()));
                    }
                    if (user.permissions() != null) {
                        authorities.addAll(user.permissions().stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()));
                    }

                    return new DefaultOAuth2AuthenticatedPrincipal(user.username(), attributes, authorities);
                });
    }
}
