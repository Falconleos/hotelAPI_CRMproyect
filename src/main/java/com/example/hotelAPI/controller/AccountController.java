package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;
import com.example.hotelAPI.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDTOResponse>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountById(@PathVariable Long id) {
        try {
            AccountDTOResponse account = accountService.getAccountById(id);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/check-in/{checkInId}")
    public ResponseEntity<Object> getAccountByCheckInId(@PathVariable Long checkInId) {
        try {
            AccountDTOResponse account = accountService.getAccountByCheckInId(checkInId);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/payments")
    public ResponseEntity<Object> addPayment(@RequestBody com.example.hotelAPI.dto.request.PaymentDTORequest request) {
        try {
            PaymentDTOResponse paymentResponse = accountService.addPaymentToAccount(request);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(paymentResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}