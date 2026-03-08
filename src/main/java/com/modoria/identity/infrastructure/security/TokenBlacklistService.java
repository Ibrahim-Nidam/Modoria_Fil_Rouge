package com.modoria.identity.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    public void blacklistToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Date expirationDate = jwtTokenProvider.getExpirationFromToken(token);
            long timeToLive = expirationDate.getTime() - System.currentTimeMillis();

            if (timeToLive > 0) {
                redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "revoked", timeToLive, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            // If token parsing fails, it's either invalid or expired anyway
        }
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}
