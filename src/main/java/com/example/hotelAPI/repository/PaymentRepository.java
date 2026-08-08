package com.example.hotelAPI.repository;

import com.example.hotelAPI.model.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    // Aquí podemos agregar consultas personalizadas de pagos si hacen falta más adelante
}