package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.PaymentDTORequest;
import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;

public interface AccountService {
    AccountDTOResponse getAccountByCheckInId(Long checkInId);
    PaymentDTOResponse addPaymentToAccount(PaymentDTORequest request);
}