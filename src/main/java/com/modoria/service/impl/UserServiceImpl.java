package com.modoria.service.impl;

import com.modoria.dto.UserDTO;
import com.modoria.mapper.UserMapper;
import com.modoria.model.User;
import com.modoria.repository.UserRepository;
import com.modoria.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(UserDTO dto){
        User user = userMapper.toEntity(dto);

        User saved = userRepository.save(user);

        return userMapper.toDTO(saved);
    }

    @Override
    public UserDTO getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not Found : " + id));

        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .toList();
    }
}
