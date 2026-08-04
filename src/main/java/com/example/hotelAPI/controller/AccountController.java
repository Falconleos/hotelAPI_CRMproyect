package com.example.hotelAPI.controller;

import com.example.hotelAPI.dto.request.PaymentDTORequest;
import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;
import com.example.hotelAPI.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/private/accounts")
@RequiredArgsConstructor
@Tag(name = "AccountManagement", description = "Endpoints para la gestión de cuentas y pagos de estadías")
public class AccountController {


    private final AccountService accountService;

    @GetMapping("/check-in/{checkInId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST', 'GUEST')")
    @Operation(summary = "Obtener el estado de cuenta y sus pagos asociados a un Check-In")
    public ResponseEntity<AccountDTOResponse> getAccountByCheckIn(@PathVariable Long checkInId) {
        return ResponseEntity.ok(accountService.getAccountByCheckInId(checkInId));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONIST')")
    @Operation(summary = "Registrar un pago (parcial o total) a una cuenta")
    public ResponseEntity<PaymentDTOResponse> addPayment(@Valid @RequestBody PaymentDTORequest request) {
        PaymentDTOResponse response = accountService.addPaymentToAccount(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
