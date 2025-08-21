package com.rin.mrlamel.common.utils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rin.mrlamel.feature.identity.model.RefreshToken;
import com.rin.mrlamel.feature.identity.model.User;
import com.rin.mrlamel.feature.identity.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.text.ParseException;
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

    RefreshTokenRepository refreshTokenRepository;


    public String generateToken(User user, long expiresIn, String tokenType) {
        if (signerKey.length() < 64) {
            throw new IllegalArgumentException("Signer key must be at least 64 characters");
        }

        List<String> permissions = user.getPermissions().stream()
                .map(permission -> permission.getName().toUpperCase())
                .toList();

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .issuer("com.rin.mrlamel")
                .subject(user.getEmail())
                .claim("id", user.getId())
                .claim("status", user.getStatus().name())
                .claim("roles", List.of("ROLE_" + user.getRole().name()))
                .claim("permissions", permissions) // Ví dụ về quyền
                .claim("token_type", tokenType) // "access_token" or "refresh_token"
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(expiresIn, ChronoUnit.MILLIS).toEpochMilli()))
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
    public Object getClaim(String token, String claimName) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getClaim(claimName);
        } catch (ParseException e) {
            throw new InvalidBearerTokenException("Malformed token", e);
        }
    }
    private Jwt extractJwt(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken token) {
            return token.getToken();
        }
        throw new IllegalStateException("Invalid authentication type");
    }

    public RefreshToken validateRefreshToken(String refreshToken) {
        try {
            // 1. Kiểm tra token null/empty
            if (refreshToken == null || refreshToken.isBlank()) {
                throw new InvalidBearerTokenException("Refresh token is empty");
            }

            // 2. Parse và verify token
            SignedJWT signedJWT = SignedJWT.parse(refreshToken);

            // 3. Verify signature (sử dụng constant-time comparison)
            JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
            if (!signedJWT.verify(verifier)) {
                throw new InvalidBearerTokenException("Invalid signature");
            }

            // 4. Lấy claims và validate
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // 5. Kiểm tra expiration
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                throw new InvalidBearerTokenException("Token has expired");
            }

            // 6. Validate required claims
            if (!"refresh_token".equals(claims.getStringClaim("token_type"))) {
                throw new InvalidBearerTokenException("Invalid token type");
            }

            String jti = getJti(refreshToken);
            // Kiểm tra trong database
            RefreshToken oRefreshToken = refreshTokenRepository.findByJti(jti)
                    .orElseThrow(() -> new InvalidBearerTokenException("Refresh token not found"));

            // Đã bị thu hồi hoặc đã hết hạn coi là không hợp lệ
            if (oRefreshToken.isRevoked() || oRefreshToken.getExpiryDate().before(new Date())) {
                throw new InvalidBearerTokenException("Refresh token is revoked or expired");
            }
            //  Trả về Object RefreshToken nếu tất cả các bước trên đều thành công
            return oRefreshToken;

        } catch (ParseException e) {
            throw new InvalidBearerTokenException("Malformed token", e);
        } catch (JOSEException e) {
            throw new InvalidBearerTokenException("Verification failed", e);
        } catch (Exception e) {
            throw new InvalidBearerTokenException("Unexpected error", e);
        }
    }
    public String getJti(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            return claimsSet.getJWTID(); // Lấy giá trị của claim "jti"
        } catch (ParseException e) {
            throw new InvalidBearerTokenException("Malformed token", e);
        }
    }


}
