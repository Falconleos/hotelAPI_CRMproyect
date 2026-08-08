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
    PaymentDTOResponse addPaymentToAccount(PaymentDTORequest request);
}