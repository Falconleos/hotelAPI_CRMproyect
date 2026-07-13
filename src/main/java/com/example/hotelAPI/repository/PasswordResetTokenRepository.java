package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.PasswordResetTokenEntity;
import com.example.hotelAPI.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
    Optional<PasswordResetTokenEntity> findByToken(String token);
    void deleteByUserEntity(UserEntity userEntity);
}