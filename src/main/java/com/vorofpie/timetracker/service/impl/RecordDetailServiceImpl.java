package com.vorofpie.timetracker.service.impl;

import com.vorofpie.timetracker.domain.RecordDetail;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.response.RecordDetailResponse;
import com.vorofpie.timetracker.mapper.RecordDetailMapper;
import com.vorofpie.timetracker.repository.RecordDetailRepository;
import com.vorofpie.timetracker.service.RecordDetailService;
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
        RecordDetail recordDetail = recordDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record detail not found"));
        return recordDetailMapper.toRecordDetailResponse(recordDetail);
    }

    @Override
    public RecordDetailResponse createRecordDetail(RecordDetailRequest recordDetailRequest) {
        RecordDetail recordDetail = recordDetailMapper.toRecordDetail(recordDetailRequest);
        recordDetail = recordDetailRepository.save(recordDetail);
        return recordDetailMapper.toRecordDetailResponse(recordDetail);
    }

    @Override
    public RecordDetailResponse updateRecordDetail(Long id, RecordDetailRequest recordDetailRequest) {
        RecordDetail existingRecordDetail = recordDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record detail not found"));
        recordDetailMapper.updateRecordDetailFromRequest(recordDetailRequest, existingRecordDetail);
        existingRecordDetail = recordDetailRepository.save(existingRecordDetail);
        return recordDetailMapper.toRecordDetailResponse(existingRecordDetail);
    }

    @Override
    public void deleteRecordDetail(Long id) {
        recordDetailRepository.deleteById(id);
    }
}
