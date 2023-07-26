package com.digital.money.msvc.api.users.repositorys;

import com.digital.money.msvc.api.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(long userId);

    @Query("select u from User u where u.dni=?1")
    Optional<User> findByDni(Long dni);

    @Query("select u from User u where u.email=?1")
    Optional<User> findByEmail(String email);
}
