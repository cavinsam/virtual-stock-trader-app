package com.vst.repository;

import com.vst.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // This method will allow us to find a user by their email address
    Optional<User> findByEmail(String email);
}

