package com.modoria.identity.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final JwtTokenProvider jwtTokenProvider;
    private final ConcurrentMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklistToken(String token) {
        String normalizedToken = normalizeToken(token);
        if (normalizedToken == null || normalizedToken.isBlank()) {
            return;
        }

        try {
            Date expirationDate = jwtTokenProvider.getExpirationFromToken(normalizedToken);
            long timeToLive = expirationDate.getTime() - System.currentTimeMillis();

            if (timeToLive > 0) {
                blacklistedTokens.put(normalizedToken, expirationDate.getTime());
            }
        } catch (Exception e) {
            log.warn("Failed to blacklist token in memory: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        String normalizedToken = normalizeToken(token);
        if (normalizedToken == null || normalizedToken.isBlank()) {
            return false;
        }

        Long expiresAt = blacklistedTokens.get(normalizedToken);
        if (expiresAt == null) {
            return false;
        }

        if (expiresAt <= System.currentTimeMillis()) {
            blacklistedTokens.remove(normalizedToken);
            return false;
        }

        return true;
    }

    private String normalizeToken(String token) {
        if (token == null) {
            return null;
        }
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
