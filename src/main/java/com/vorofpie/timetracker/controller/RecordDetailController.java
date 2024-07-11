package com.vorofpie.timetracker.controller;

import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.response.RecordDetailResponse;
import com.vorofpie.timetracker.service.RecordDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/records")
public class RecordDetailController {

    private final RecordDetailService recordDetailService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecordDetailResponse createRecordDetail(@RequestBody @Valid RecordDetailRequest RecordDetailRequest) {
        return recordDetailService.createRecordDetail(RecordDetailRequest);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecordDetailResponse updateRecordDetail(@PathVariable Long id, @RequestBody @Valid RecordDetailRequest recordDetailRequest) {
        return recordDetailService.updateRecordDetail(id, recordDetailRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecordDetail(@PathVariable Long id) {
        recordDetailService.deleteRecordDetail(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecordDetailResponse getRecordDetailById(@PathVariable Long id) {
        return recordDetailService.getRecordDetailById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RecordDetailResponse> getAllRecordDetails() {
        return recordDetailService.getAllRecordDetails();
    }
}
