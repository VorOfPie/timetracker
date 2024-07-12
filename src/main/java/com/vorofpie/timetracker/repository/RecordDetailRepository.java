package com.vorofpie.timetracker.repository;

import com.vorofpie.timetracker.domain.RecordDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordDetailRepository extends JpaRepository<RecordDetail, Long> {
    List<RecordDetail> findByTask_Project_Users_Id(Long id);
}