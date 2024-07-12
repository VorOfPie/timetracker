package com.vorofpie.timetracker.repository;

import com.vorofpie.timetracker.domain.TaskDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface TaskDetailRepository extends JpaRepository<TaskDetail, Long> {
    List<TaskDetail> findByProject_Users_Id(Long id);
}