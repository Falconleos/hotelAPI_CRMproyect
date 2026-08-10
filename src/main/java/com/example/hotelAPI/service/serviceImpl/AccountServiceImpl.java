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
import com.example.hotelAPI.repository.BookingRepository; // Asegúrate de tener este import
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
    private final BookingRepository bookingRepository; // Añadir esta dependencia
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

        account.addRoomServiceCharge(amount);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void subtractChargeFromAccount(Long checkInId, Double amount) {
        AccountEntity account = accountRepository.findByCheckInId(checkInId)
                .orElseThrow(() -> new EntityNotFoundException("Cuenta no encontrada para el check-in ID: " + checkInId));

        account.setTotalAmount(account.getTotalAmount() - amount);
        account.recalculateAccount();
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public PaymentDTOResponse addPaymentToAccount(PaymentDTORequest request) {
        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Cuenta no encontrada con ID: " + request.getAccountId()));

        // Obtener el usuario logueado actual desde el contexto de seguridad
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

        // Si vas a persistir el nombre y apellido en la entidad de pago:
        payment.setRegisteredByName(loggedUserName);
        payment.setRegisteredBySurname(loggedUserSurname);

        account.addPayment(payment);
        accountRepository.save(account);

        PaymentEntity savedPayment = account.getPayments().get(account.getPayments().size() - 1);

        // Mapear al DTO asegurando que viajen los datos del usuario logueado
        PaymentDTOResponse response = paymentMapper.toDto(savedPayment);
        response.setRegisteredByName(loggedUserName);
        response.setRegisteredBySurname(loggedUserSurname);

        return response;
    }

    // --- NUEVOS MÉTODOS PARA SEÑAS DE RESERVAS ---

    @Override
    @Transactional
    public PaymentDTOResponse addPaymentToBooking(PaymentDTORequest request) {
        BookingEntity booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada con ID: " + request.getBookingId()));

        // Calcular la suma de las señas ya registradas
        double currentPaymentsTotal = booking.getPayments().stream()
                .mapToDouble(PaymentEntity::getAmount)
                .sum();

        // Verificar si el nuevo pago excede el total de la reserva
        if (currentPaymentsTotal + request.getAmount() > booking.getTotalPrice()) {
            double remainingBalance = booking.getTotalPrice() - currentPaymentsTotal;
            throw new IllegalArgumentException("El monto de la seña excede el saldo restante de la reserva. Saldo pendiente: $" + remainingBalance);
        }

        // Obtener el usuario logueado actual desde el contexto de seguridad
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

        // Asignar los datos del usuario logueado
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