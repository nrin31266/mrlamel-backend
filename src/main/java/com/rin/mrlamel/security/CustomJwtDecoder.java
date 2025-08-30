package com.rin.mrlamel.security;

import com.nimbusds.jose.JWSAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${jwt.signerKey}")
    private String signerKey;
    private JwtDecoder jwtDecoder;

    @PostConstruct // Được gọi SAU KHI Spring inject dependencies
    public void init() {
        log.info("Initializing CustomJwtDecoder with signerKey of length: {}", signerKey.length());
        SecretKey key = new SecretKeySpec(signerKey.getBytes(), JWSAlgorithm.HS512.getName());
        this.jwtDecoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        log.info("Decoding JWT token: {}", token);

        return jwtDecoder.decode(token);
    }

}
