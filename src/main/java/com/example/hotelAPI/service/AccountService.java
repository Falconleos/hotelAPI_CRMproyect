package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.PaymentDTORequest;
import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;

import java.util.List;

public interface AccountService {
    List<AccountDTOResponse> getAllAccounts();
    AccountDTOResponse getAccountById(Long id);
    AccountDTOResponse getAccountByCheckInId(Long checkInId);
    void addChargeToAccount(Long checkInId, Double amount);
    void subtractChargeFromAccount(Long checkInId, Double amount);

    // Nuevo método para actualizar el porcentaje de ajuste
    AccountDTOResponse updateAdjustmentPercentage(Long checkInId, Integer adjustmentPercentage);

    PaymentDTOResponse addPaymentToAccount(PaymentDTORequest request);

    // Nuevos métodos para reservas
    PaymentDTOResponse addPaymentToBooking(PaymentDTORequest request);
    List<PaymentDTOResponse> getPaymentsByBookingId(Long bookingId);
}