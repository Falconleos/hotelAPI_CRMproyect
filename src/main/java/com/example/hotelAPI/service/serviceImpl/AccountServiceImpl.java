package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.PaymentDTORequest;
import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;
import com.example.hotelAPI.mappers.AccountMapper;
import com.example.hotelAPI.mappers.PaymentMapper;
import com.example.hotelAPI.model.AccountEntity;
import com.example.hotelAPI.model.PaymentEntity;
import com.example.hotelAPI.repository.AccountRepository;
import com.example.hotelAPI.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AccountDTOResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTOResponse getAccountById(Long id) {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada con ID: " + id));
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDTOResponse getAccountByCheckInId(Long checkInId) {
        AccountEntity account = accountRepository.findByCheckInId(checkInId)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada para el check-in ID: " + checkInId));
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public void addChargeToAccount(Long checkInId, Double amount) {
        AccountEntity account = accountRepository.findByCheckInId(checkInId)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada para el check-in ID: " + checkInId));

        account.addRoomServiceCharge(amount); // Suma el cargo y recalcula
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void subtractChargeFromAccount(Long checkInId, Double amount) {
        AccountEntity account = accountRepository.findByCheckInId(checkInId)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada para el check-in ID: " + checkInId));

        // Resta el monto del total y recalcula
        account.setTotalAmount(account.getTotalAmount() - amount);
        account.recalculateAccount();
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public PaymentDTOResponse addPaymentToAccount(PaymentDTORequest request) {
        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Cuenta no encontrada con ID: " + request.getAccountId()));

        // Usamos el PaymentMapper para convertir el Request a Entity
        PaymentEntity payment = paymentMapper.toEntity(request);

        // Le asignamos la fecha actual ya que se ignora en el mapper
        payment.setPaymentDate(java.time.LocalDateTime.now());

        // Utiliza el método de negocio de AccountEntity para añadir el pago y recalcular
        account.addPayment(payment);

        accountRepository.save(account);

        // Obtenemos el pago recién guardado (el último de la lista) y lo mapeamos a DTO con Mapstruct
        PaymentEntity savedPayment = account.getPayments().get(account.getPayments().size() - 1);
        return paymentMapper.toDto(savedPayment);
    }
}