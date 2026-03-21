package com.modoria.identity.application.service.user;

import com.modoria.identity.application.mapper.user.UserMapper;
import com.modoria.identity.domain.model.User;
import com.modoria.identity.domain.repository.RoleRepository;
import com.modoria.identity.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void deleteAdminUser_marksUserAsDeletedAndDisabled() {
        User user = User.builder()
                .id(1L)
                .enabled(true)
                .deleted(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.deleteAdminUser(1L);

        assertThat(user.getDeleted()).isTrue();
        assertThat(user.getEnabled()).isFalse();
        verify(userRepository).save(user);
    }
}
