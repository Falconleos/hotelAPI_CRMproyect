package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.RefreshTokenEntity;
import com.example.hotelAPI.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity,Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    @Modifying
    void deleteByUserEntity(UserEntity userEntity);
}
