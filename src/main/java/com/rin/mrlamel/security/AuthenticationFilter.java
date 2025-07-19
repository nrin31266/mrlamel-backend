package com.rin.mrlamel.security;

import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@Slf4j
public class AuthenticationFilter {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CustomJwtDecoder customJwtDecoder, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        // Debugging
        // Configuring the security filter chain
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .cors(cors -> cors.configurationSource(request -> {
                    var config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(allowedOrigins.split(",")));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    config.setAllowCredentials(true);
                    config.setExposedHeaders(List.of("Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(customJwtConverter())
                ).authenticationEntryPoint(customAuthenticationEntryPoint));


        return httpSecurity.build();
    }
    @Bean
    Converter<Jwt, AbstractAuthenticationToken> customJwtConverter() {
        return jwt -> {
            String username = jwt.getSubject();
            List<String> roles = jwt.getClaim("roles");

            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new JwtAuthenticationToken(jwt, authorities, username);
        };
    }


}
