package com.modoria.identity.infrastructure.security;

import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    @Test
    void loadUserByUsername_returnsUserDetails() {
        User user = User.builder().id(1L).email("user@test.com").password("x").enabled(true).build();
        when(userRepository.findByEmailAndDeletedFalse("user@test.com")).thenReturn(Optional.of(user));

        CustomUserDetails details = (CustomUserDetails) service.loadUserByUsername("user@test.com");

        assertThat(details.getUsername()).isEqualTo("user@test.com");
    }
}
