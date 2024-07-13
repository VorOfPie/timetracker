package com.vorofpie.timetracker.repository;

import com.vorofpie.timetracker.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUsers_Id(Long userId);
}