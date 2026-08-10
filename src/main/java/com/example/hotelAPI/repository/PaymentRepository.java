package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query("SELECT p FROM PaymentEntity p " +
            "LEFT JOIN FETCH p.account a " +
            "LEFT JOIN FETCH a.checkIn c " +
            "LEFT JOIN FETCH c.userEntity")
    List<PaymentEntity> findAllWithDetails();
}