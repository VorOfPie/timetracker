package com.vorofpie.timetracker.service;

import com.vorofpie.timetracker.dto.request.UserRequest;
import com.vorofpie.timetracker.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse createUser(UserRequest userRequest);

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);
}
