package com.vorofpie.timetracker.repository;

import com.vorofpie.timetracker.domain.RecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordDetailRepository extends JpaRepository<RecordDetail, Long> {
}