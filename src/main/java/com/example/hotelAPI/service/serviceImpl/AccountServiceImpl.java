package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.PaymentDTORequest;
import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;
import com.example.hotelAPI.mappers.AccountMapper;
import com.example.hotelAPI.mappers.PaymentMapper;
import com.example.hotelAPI.model.AccountEntity;
import com.example.hotelAPI.model.PaymentEntity;
import com.example.hotelAPI.repository.AccountRepository;
import com.example.hotelAPI.repository.PaymentRepository;
import com.example.hotelAPI.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository; // Se puede mantener o prescindir si usas cascada, pero lo dejamos por compatibilidad
    private final AccountMapper accountMapper;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public AccountDTOResponse getAccountByCheckInId(Long checkInId) {
        AccountEntity account = accountRepository.findByCheckInId(checkInId)
                .orElseThrow(() -> new RuntimeException("Account not found for Check-In ID: " + checkInId));
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public PaymentDTOResponse addPaymentToAccount(PaymentDTORequest request) {
        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found with ID: " + request.getAccountId()));

        if (Boolean.TRUE.equals(account.getIsPaid())) {
            throw new IllegalStateException("This account is already fully paid.");
        }

        // Crear la entidad de pago
        PaymentEntity payment = PaymentEntity.builder()
                .amount(request.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(request.getPaymentMethod())
                .transactionReference(request.getTransactionReference())
                .build();

        // Agregar el pago a la cuenta (esto recalcula totales y el isPaid de la cuenta internamente)
        account.addPayment(payment);

        // Guardamos la cuenta (actualiza saldos y persiste el pago en cascada)
        AccountEntity savedAccount = accountRepository.save(account);

        // Obtenemos el pago recién guardado (el último de la lista) para retornarlo en el DTO
        PaymentEntity savedPayment = savedAccount.getPayments().get(savedAccount.getPayments().size() - 1);

        return paymentMapper.toDto(savedPayment);
    }
}