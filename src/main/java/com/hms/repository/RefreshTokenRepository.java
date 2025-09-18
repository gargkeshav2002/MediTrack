package com.hms.repository;

import com.hms.entity.RefreshToken;
import com.hms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // returns number of rows deleted
    int deleteByUser(User user);
}
