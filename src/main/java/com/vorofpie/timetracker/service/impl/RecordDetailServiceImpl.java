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

@Service
@RequiredArgsConstructor
public class RecordDetailServiceImpl implements RecordDetailService {

    private final RecordDetailRepository recordDetailRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final RecordDetailMapper recordDetailMapper;
    private final UserRepository userRepository;

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

    @ProjectMemberAccess
    @Override
    public RecordDetailResponse getRecordDetailById(Long id) {
        RecordDetail recordDetail = findRecordDetailByIdOrThrow(id);
        return recordDetailMapper.toRecordDetailResponse(recordDetail);
    }

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

    @ProjectMemberAccess
    @Override
    public void deleteRecordDetail(Long id) {
        recordDetailRepository.deleteById(id);
    }

    private RecordDetail findRecordDetailByIdOrThrow(Long id) {
        return recordDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(RESOURCE_NOT_FOUND_MESSAGE, "Record detail", id)));
    }
}
