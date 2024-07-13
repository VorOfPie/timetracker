package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.aspect.annotation.ProjectMemberAccess;
import com.vorofpie.timetracker.config.SecurityUser;
import com.vorofpie.timetracker.domain.RecordDetail;
import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.response.RecordDetailResponse;
import com.vorofpie.timetracker.error.exception.ResourceNotFoundException;
import com.vorofpie.timetracker.mapper.RecordDetailMapper;
import com.vorofpie.timetracker.repository.RecordDetailRepository;
import com.vorofpie.timetracker.repository.TaskDetailRepository;
import com.vorofpie.timetracker.repository.UserRepository;
import com.vorofpie.timetracker.service.RecordDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.vorofpie.timetracker.domain.RoleName.ADMIN;
import static com.vorofpie.timetracker.error.ErrorMessages.*;

/**
 * Implementation of the RecordDetailService interface, providing operations for managing record details.
 *
 * <p>This service is responsible for CRUD operations on record details. It checks the user's role and access level
 * to ensure that users can only access or modify record details they have permissions for. The @ProjectMemberAccess
 * annotation is used to enforce access control based on the user's association with the project related to the record detail.</p>
 */
@Service
@RequiredArgsConstructor
public class RecordDetailServiceImpl implements RecordDetailService {

    private final RecordDetailRepository recordDetailRepository; // Repository for RecordDetail entity
    private final TaskDetailRepository taskDetailRepository; // Repository for TaskDetail entity
    private final RecordDetailMapper recordDetailMapper; // Mapper for converting between RecordDetail entities and DTOs
    private final UserRepository userRepository; // Repository for User entity

    /**
     * Retrieves all record details. If the user is an admin, all record details are returned; otherwise, only those associated with the user's tasks are returned.
     *
     * <p>Admins have access to all record details. Non-admin users can only see record details associated with tasks
     * that they are assigned to, based on their user ID.</p>
     *
     * @return a list of record detail responses
     */
    @Override
    public List<RecordDetailResponse> getAllRecordDetails() {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = securityUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + ADMIN.name()));

        List<RecordDetail> recordDetails;
        if (isAdmin) {
            recordDetails = recordDetailRepository.findAll();
        } else {
            recordDetails = recordDetailRepository.findByTask_Project_Users_Id(securityUser.user().getId());
        }

        return recordDetails.stream()
                .map(recordDetailMapper::toRecordDetailResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a record detail by its ID. Requires that the user has access to the record detail.
     *
     * <p>This method uses the @ProjectMemberAccess annotation to enforce access control. It ensures that the current user
     * is a member of the project related to the record detail before allowing access.</p>
     *
     * @param id the ID of the record detail
     * @return the record detail response
     */
    @ProjectMemberAccess
    @Override
    public RecordDetailResponse getRecordDetailById(Long id) {
        RecordDetail recordDetail = findRecordDetailByIdOrThrow(id);
        return recordDetailMapper.toRecordDetailResponse(recordDetail);
    }

    /**
     * Creates a new record detail with the provided details.
     *
     * <p>The @ProjectMemberAccess annotation ensures that the user has appropriate access to the project associated with
     * the record detail. If the user is not authorized, the creation will be blocked by the aspect logic.</p>
     *
     * @param recordDetailRequest the details of the record detail to create
     * @return the created record detail response
     */
    @ProjectMemberAccess
    @Override
    public RecordDetailResponse createRecordDetail(RecordDetailRequest recordDetailRequest) {
        TaskDetail taskDetail = taskDetailRepository.findById(recordDetailRequest.taskId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Task", recordDetailRequest.taskId())));
        RecordDetail recordDetail = recordDetailMapper.toRecordDetail(recordDetailRequest);
        recordDetail.setTask(taskDetail);
        taskDetail.getRecordDetails().add(recordDetail);
        User user = userRepository.findByEmail(recordDetailRequest.userEmail())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, recordDetailRequest.userEmail())));
        recordDetail.setUser(user);
        taskDetailRepository.save(taskDetail);
        return recordDetailMapper.toRecordDetailResponse(recordDetailRepository.save(recordDetail));
    }

    /**
     * Updates an existing record detail with the provided details.
     *
     * <p>The @ProjectMemberAccess annotation is used to enforce that the current user has the necessary permissions
     * to update the record detail. This ensures that only authorized users can modify record details related to their projects.</p>
     *
     * @param id the ID of the record detail to update
     * @param recordDetailRequest the new details for the record detail
     * @return the updated record detail response
     */
    @ProjectMemberAccess
    @Override
    public RecordDetailResponse updateRecordDetail(Long id, RecordDetailRequest recordDetailRequest) {
        RecordDetail existingRecordDetail = findRecordDetailByIdOrThrow(id);
        recordDetailMapper.updateRecordDetailFromRequest(recordDetailRequest, existingRecordDetail);
        User user = userRepository.findByEmail(recordDetailRequest.userEmail())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MESSAGE, recordDetailRequest.userEmail())));
        existingRecordDetail.setUser(user);
        existingRecordDetail = recordDetailRepository.save(existingRecordDetail);
        return recordDetailMapper.toRecordDetailResponse(existingRecordDetail);
    }

    /**
     * Deletes a record detail by its ID.
     *
     * <p>The @ProjectMemberAccess annotation ensures that only users with the correct permissions can delete record details
     * associated with their projects. If the user does not have the necessary permissions, the deletion will be blocked by the aspect logic.</p>
     *
     * @param id the ID of the record detail to delete
     */
    @ProjectMemberAccess
    @Override
    public void deleteRecordDetail(Long id) {
        recordDetailRepository.deleteById(id);
    }

    /**
     * Finds a record detail by its ID or throws an exception if not found.
     *
     * <p>This helper method is used to retrieve a record detail from the repository and throw a ResourceNotFoundException
     * if the record detail does not exist.</p>
     *
     * @param id the ID of the record detail
     * @return the record detail entity
     * @throws ResourceNotFoundException if the record detail is not found
     */
    private RecordDetail findRecordDetailByIdOrThrow(Long id) {
        return recordDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Record detail", id)));
    }
}
