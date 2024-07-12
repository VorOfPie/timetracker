package com.vorofpie.timetracker.repository;


import com.vorofpie.timetracker.domain.Role;
import com.vorofpie.timetracker.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName roleName);

}
