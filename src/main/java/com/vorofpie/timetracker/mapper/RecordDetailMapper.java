package com.vorofpie.timetracker.mapper;

import com.vorofpie.timetracker.domain.RecordDetail;
import com.vorofpie.timetracker.dto.request.CreateRecordDetailRequest;
import com.vorofpie.timetracker.dto.request.RecordDetailRequest;
import com.vorofpie.timetracker.dto.response.RecordDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecordDetailMapper {

    RecordDetailResponse toRecordDetailResponse(RecordDetail recordDetail);

    RecordDetail toRecordDetail(RecordDetailRequest recordDetailRequest);

    RecordDetail toRecordDetail(CreateRecordDetailRequest recordDetailRequest);

    @Mapping(target = "id", ignore = true)
    void updateRecordDetailFromRequest(RecordDetailRequest recordDetailRequest, @MappingTarget RecordDetail recordDetail);
}
