package com.digital.money.msvc.api.users.repositorys;

import com.digital.money.msvc.api.users.entities.Verified;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVerificationRepository extends JpaRepository<Verified, Long> {
}
