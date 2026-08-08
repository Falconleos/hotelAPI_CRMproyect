package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    // Buscar una cuenta a partir del ID del Check-In vinculado
    Optional<AccountEntity> findByCheckInId(Long checkInId);
}