package com.vorofpie.timetracker.service;

import com.vorofpie.timetracker.dto.request.CreateRecordDetailRequest;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.response.RecordDetailResponse;

import java.util.List;

public interface RecordDetailService {

    List<RecordDetailResponse> getAllRecordDetails();

    RecordDetailResponse getRecordDetailById(Long id);

    RecordDetailResponse createRecordDetail(CreateRecordDetailRequest recordDetailRequest);

    RecordDetailResponse updateRecordDetail(Long id, RecordDetailRequest recordDetailRequest);

    void deleteRecordDetail(Long id);
}
