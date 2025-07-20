package com.rin.mrlamel.feature.identity.repository;

import com.rin.mrlamel.feature.identity.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Checks if a token is invalidated.
     *
     * @param token the token to check
     * @return true if the token is invalidated, false otherwise
     */
    Optional<RefreshToken> findByJti(String jti);
    Optional<RefreshToken> findByUserIdAndDeviceId(Long userId, String deviceId);

    /**
     * Deletes all invalidated tokens.
     */
    void deleteAll();
}
