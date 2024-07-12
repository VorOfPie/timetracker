package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.aspect.annotation.EmailMatchOrAdminAccess;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.UserRequest;
import com.vorofpie.timetracker.dto.response.UserResponse;
import com.vorofpie.timetracker.mapper.UserMapper;
import com.vorofpie.timetracker.repository.UserRepository;
import com.vorofpie.timetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @EmailMatchOrAdminAccess
    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserByIdOrThrow(id);
        return userMapper.toUserResponse(user);
    }
    @EmailMatchOrAdminAccess
    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = findUserByIdOrThrow(id);
        userMapper.updateUserFromRequest(userRequest, existingUser);
        existingUser = userRepository.save(existingUser);
        return userMapper.toUserResponse(existingUser);
    }
    @EmailMatchOrAdminAccess
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
