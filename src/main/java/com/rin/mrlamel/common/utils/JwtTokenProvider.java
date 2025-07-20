package com.rin.mrlamel.common.utils;

import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.rin.mrlamel.feature.identity.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenProvider {
    @NonFinal
    @Value("${jwt.signerKey}")
    String signerKey;


    public String generateToken(User user, long expiresIn) {
        if (signerKey.length() < 64) {
            throw new IllegalArgumentException("Signer key must be at least 64 characters");
        }
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .issuer("com.rin.mrlamel")
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("status", user.getStatus().name())
                .claim("roles", List.of("ROLE_" + user.getRole().name()))
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(expiresIn, ChronoUnit.SECONDS).toEpochMilli()))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try{
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        }catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }
    public String getSubject(Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        return jwt.getSubject(); // = claim "sub"
    }
    public Object getClaim(Authentication authentication, String claimName) {
        Jwt jwt = extractJwt(authentication);
        return jwt.getClaim(claimName); // có thể là "roles", "id", "status", ...
    }

    private Jwt extractJwt(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken token) {
            return token.getToken();
        }
        throw new IllegalStateException("Invalid authentication type");
    }

}
