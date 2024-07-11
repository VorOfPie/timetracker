package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.domain.Project;
import com.vorofpie.timetracker.domain.RecordDetail;
import com.vorofpie.timetracker.dto.request.CreateRecordDetailRequest;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.response.RecordDetailResponse;
import com.vorofpie.timetracker.mapper.RecordDetailMapper;
import com.vorofpie.timetracker.repository.ProjectRepository;
import com.vorofpie.timetracker.repository.RecordDetailRepository;
import com.vorofpie.timetracker.service.RecordDetailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordDetailServiceImpl implements RecordDetailService {

    private final RecordDetailRepository recordDetailRepository;
    private final RecordDetailMapper recordDetailMapper;


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
    public RecordDetailResponse createRecordDetail(CreateRecordDetailRequest createRecordDetailRequest) {
        RecordDetail recordDetail = recordDetailMapper.toRecordDetail(createRecordDetailRequest);
        return recordDetailMapper.toRecordDetailResponse(recordDetailRepository.save(recordDetail));
    }

    @Override
    public RecordDetailResponse updateRecordDetail(Long id, RecordDetailRequest recordDetailRequest) {
        RecordDetail existingRecordDetail = findRecordDetailByIdOrThrow(id);
        recordDetailMapper.updateRecordDetailFromRequest(recordDetailRequest, existingRecordDetail);
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
