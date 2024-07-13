package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.aspect.annotation.EmailMatchOrAdminAccess;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.UserRequest;
import com.vorofpie.timetracker.dto.response.UserResponse;
import com.vorofpie.timetracker.error.exception.ResourceNotFoundException;
import com.vorofpie.timetracker.mapper.UserMapper;
import com.vorofpie.timetracker.repository.UserRepository;
import com.vorofpie.timetracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.vorofpie.timetracker.error.ErrorMessages.USER_NOT_FOUND_MESSAGE;

/**
 * Service implementation for managing user details.
 * <p>
 * This service provides methods to retrieve, update, and delete user details.
 * Access to these operations is controlled based on the user's email or admin role.
 *
 * @see EmailMatchOrAdminAccess
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Retrieves a list of all users.
     * <p>
     * This method does not require any special access control as it is typically intended for administrative purposes
     * and is accessible by any authorized user.
     *
     * @return a list of user responses
     */
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID.
     * <p>
     * This method is annotated with {@link EmailMatchOrAdminAccess} to ensure that only users whose email matches the requested
     * user or an admin can access the user details.
     *
     * @param id the ID of the user to retrieve
     * @return the user response
     * @throws ResourceNotFoundException if the user is not found
     */
    @EmailMatchOrAdminAccess
    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserByIdOrThrow(id);
        return userMapper.toUserResponse(user);
    }

    /**
     * Updates an existing user.
     * <p>
     * This method is annotated with {@link EmailMatchOrAdminAccess} to ensure that only users whose email matches the requested
     * user or an admin can update the user details.
     *
     * @param id          the ID of the user to update
     * @param userRequest the request object containing updated user details
     * @return the updated user response
     * @throws ResourceNotFoundException if the user is not found
     */
    @EmailMatchOrAdminAccess
    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = findUserByIdOrThrow(id);
        userMapper.updateUserFromRequest(userRequest, existingUser);
        existingUser = userRepository.save(existingUser);
        return userMapper.toUserResponse(existingUser);
    }

    /**
     * Deletes a user by their ID.
     * <p>
     * This method is annotated with {@link EmailMatchOrAdminAccess} to ensure that only users whose email matches the requested
     * user or an admin can delete the user.
     *
     * @param id the ID of the user to delete
     */
    @EmailMatchOrAdminAccess
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Finds a user by their ID or throws an exception if not found.
     *
     * @param id the ID of the user
     * @return the user
     * @throws ResourceNotFoundException if the user is not found
     */
    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, id)));
    }
}
