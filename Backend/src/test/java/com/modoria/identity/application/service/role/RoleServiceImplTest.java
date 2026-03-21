package com.modoria.identity.application.service.role;

import com.modoria.identity.application.dto.role.RoleDTO;
import com.modoria.identity.application.mapper.role.RoleMapper;
import com.modoria.identity.domain.model.Role;
import com.modoria.identity.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl service;

    @Test
    void getAllRoles_returnsMappedDtos() {
        Role role = Role.builder().id(1L).name("ADMIN").build();
        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(roleMapper.toDTO(role)).thenReturn(new RoleDTO(1L, "ADMIN"));

        List<RoleDTO> result = service.getAllRoles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("ADMIN");
    }
}
