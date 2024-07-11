package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.domain.RecordDetail;
import com.vorofpie.timetracker.domain.TaskDetail;
import com.vorofpie.timetracker.domain.User;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.response.RecordDetailResponse;
import com.vorofpie.timetracker.mapper.RecordDetailMapper;
import com.vorofpie.timetracker.repository.RecordDetailRepository;
import com.vorofpie.timetracker.repository.TaskDetailRepository;
import com.vorofpie.timetracker.repository.UserRepository;
import com.vorofpie.timetracker.service.RecordDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordDetailServiceImpl implements RecordDetailService {

    private final RecordDetailRepository recordDetailRepository;
    private final TaskDetailRepository taskDetailRepository;
    private final RecordDetailMapper recordDetailMapper;
    private final UserRepository userRepository;


    @Override
    public List<RecordDetailResponse> getAllRecordDetails() {
        return recordDetailRepository.findAll().stream()
                .map(recordDetailMapper::toRecordDetailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RecordDetailResponse getRecordDetailById(Long id) {
        RecordDetail recordDetail = findRecordDetailByIdOrThrow(id);
        return recordDetailMapper.toRecordDetailResponse(recordDetail);
    }

    @Override
    public RecordDetailResponse createRecordDetail(RecordDetailRequest recordDetailRequest) {
        TaskDetail taskDetail = taskDetailRepository.findById(recordDetailRequest.taskId())
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + recordDetailRequest.taskId() + " not found"));
        RecordDetail recordDetail = recordDetailMapper.toRecordDetail(recordDetailRequest);
        recordDetail.setTask(taskDetail);
        taskDetail.getRecordDetails().add(recordDetail);
        User user = userRepository.findByEmail(recordDetailRequest.userEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + recordDetailRequest.userEmail() + " not found"));
        recordDetail.setUser(user);
        taskDetailRepository.save(taskDetail);
        return recordDetailMapper.toRecordDetailResponse(recordDetailRepository.save(recordDetail));
    }



    @Override
    public RecordDetailResponse updateRecordDetail(Long id, RecordDetailRequest recordDetailRequest) {
        RecordDetail existingRecordDetail = findRecordDetailByIdOrThrow(id);
        recordDetailMapper.updateRecordDetailFromRequest(recordDetailRequest, existingRecordDetail);
        User user = userRepository.findByEmail(recordDetailRequest.userEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with email " + recordDetailRequest.userEmail() + " not found"));
        existingRecordDetail.setUser(user);
        existingRecordDetail = recordDetailRepository.save(existingRecordDetail);
        return recordDetailMapper.toRecordDetailResponse(existingRecordDetail);
    }


    @Override
    public void deleteRecordDetail(Long id) {
        recordDetailRepository.deleteById(id);
    }

    private RecordDetail findRecordDetailByIdOrThrow(Long id) {
        return recordDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record detail not found"));
    }
}
