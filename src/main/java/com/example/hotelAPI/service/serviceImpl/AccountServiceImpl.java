package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.PaymentDTORequest;
import com.example.hotelAPI.dto.response.AccountDTOResponse;
import com.example.hotelAPI.dto.response.PaymentDTOResponse;
import com.example.hotelAPI.exceptions.UserNotFoundException;
import com.example.hotelAPI.mappers.AccountMapper;
import com.example.hotelAPI.mappers.PaymentMapper;
import com.example.hotelAPI.model.AccountEntity;
import com.example.hotelAPI.model.BookingEntity;
import com.example.hotelAPI.model.PaymentEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.AccountRepository;
import com.example.hotelAPI.repository.BookingRepository;
import com.example.hotelAPI.repository.UserRepository;
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
    private final BookingRepository bookingRepository;
    private final AccountMapper accountMapper;
    private final PaymentMapper paymentMapper;
    private final UserRepository userRepository;

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

        // Sumamos al total de servicios extra, dejando baseAmount totalmente intacto
        double currentServices = account.getServicesTotal() != null ? account.getServicesTotal() : 0.0;
        double addedAmount = amount != null ? amount : 0.0;
        account.setServicesTotal(currentServices + addedAmount);

        account.recalculateAccount();
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void subtractChargeFromAccount(Long checkInId, Double amount) {
        AccountEntity account = accountRepository.findByCheckInId(checkInId)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada para el check-in ID: " + checkInId));

        // Restamos del total de servicios extra de forma segura sin tocar baseAmount
        double currentServices = account.getServicesTotal() != null ? account.getServicesTotal() : 0.0;
        double subAmount = amount != null ? amount : 0.0;
        account.setServicesTotal(Math.max(0.0, currentServices - subAmount));

        account.recalculateAccount();
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public AccountDTOResponse updateAdjustmentPercentage(Long checkInId, Integer adjustmentPercentage) {
        AccountEntity account = accountRepository.findByCheckInId(checkInId)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada para el check-in ID: " + checkInId));

        account.setAdjustmentPercentage(adjustmentPercentage != null ? adjustmentPercentage : 0);
        account.recalculateAccount();

        AccountEntity savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public PaymentDTOResponse addPaymentToAccount(PaymentDTORequest request) {
        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Cuenta no encontrada con ID: " + request.getAccountId()));

        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String loggedUserName = null;
        String loggedUserSurname = null;

        if (authentication != null && authentication.getPrincipal() instanceof com.example.hotelAPI.security.CustomUserDetails) {
            com.example.hotelAPI.security.CustomUserDetails userDetails = (com.example.hotelAPI.security.CustomUserDetails) authentication.getPrincipal();

            UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(()->new UserNotFoundException("username inexistent"));

            loggedUserName = user.getName();
            loggedUserSurname = user.getSurname();
        }

        PaymentEntity payment = paymentMapper.toEntity(request);
        payment.setPaymentDate(java.time.LocalDateTime.now());

        payment.setRegisteredByName(loggedUserName);
        payment.setRegisteredBySurname(loggedUserSurname);

        account.addPayment(payment);
        accountRepository.save(account);

        PaymentEntity savedPayment = account.getPayments().get(account.getPayments().size() - 1);

        PaymentDTOResponse response = paymentMapper.toDto(savedPayment);
        response.setRegisteredByName(loggedUserName);
        response.setRegisteredBySurname(loggedUserSurname);

        return response;
    }

    @Override
    @Transactional
    public PaymentDTOResponse addPaymentToBooking(PaymentDTORequest request) {
        BookingEntity booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + request.getBookingId()));

        double currentPaymentsTotal = booking.getPayments().stream()
                .mapToDouble(PaymentEntity::getAmount)
                .sum();

        if (currentPaymentsTotal + request.getAmount() > booking.getTotalPrice()) {
            double remainingBalance = booking.getTotalPrice() - currentPaymentsTotal;
            throw new IllegalArgumentException("El monto de la seña excede el saldo restante de la reserva. Saldo pendiente: $" + remainingBalance);
        }

        var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String loggedUserName = null;
        String loggedUserSurname = null;

        if (authentication != null && authentication.getPrincipal() instanceof com.example.hotelAPI.security.CustomUserDetails) {
            com.example.hotelAPI.security.CustomUserDetails userDetails = (com.example.hotelAPI.security.CustomUserDetails) authentication.getPrincipal();

            UserEntity user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("username inexistent"));

            loggedUserName = user.getName();
            loggedUserSurname = user.getSurname();
        }

        PaymentEntity payment = paymentMapper.toEntity(request);
        payment.setPaymentDate(java.time.LocalDateTime.now());
        payment.setBooking(booking);

        payment.setRegisteredByName(loggedUserName);
        payment.setRegisteredBySurname(loggedUserSurname);

        booking.getPayments().add(payment);
        bookingRepository.save(booking);

        PaymentEntity savedPayment = booking.getPayments().get(booking.getPayments().size() - 1);

        PaymentDTOResponse response = paymentMapper.toDto(savedPayment);
        response.setRegisteredByName(loggedUserName);
        response.setRegisteredBySurname(loggedUserSurname);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTOResponse> getPaymentsByBookingId(Long bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + bookingId));

        return booking.getPayments().stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
    }
}