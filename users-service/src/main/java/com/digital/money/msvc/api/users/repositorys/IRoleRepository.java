package com.digital.money.msvc.api.users.repositorys;

import com.digital.money.msvc.api.users.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findById(Integer roleId);
}
