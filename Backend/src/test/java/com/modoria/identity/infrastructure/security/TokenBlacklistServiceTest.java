package com.modoria.identity.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private TokenBlacklistService service;

    @Test
    void blacklistToken_thenTokenIsBlacklisted() {
        String token = "token-123";
        when(jwtTokenProvider.getExpirationFromToken(token)).thenReturn(new Date(System.currentTimeMillis() + 60_000));

        service.blacklistToken("Bearer " + token);

        assertThat(service.isBlacklisted(token)).isTrue();
    }
}
