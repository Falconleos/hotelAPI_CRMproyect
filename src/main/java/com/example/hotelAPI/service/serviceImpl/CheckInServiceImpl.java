package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.BookingCancellationDTORequest;
import com.example.hotelAPI.dto.request.CheckInDTORequest;
import com.example.hotelAPI.dto.response.CheckInDTOResponse;
import com.example.hotelAPI.dto.response.UserDtoResponse;
import com.example.hotelAPI.enums.BookingState;
import com.example.hotelAPI.enums.CheckInState;
import com.example.hotelAPI.enums.RoomState;
import com.example.hotelAPI.exceptions.*;
import com.example.hotelAPI.mappers.CheckInMapper;
import com.example.hotelAPI.model.*;
import com.example.hotelAPI.repository.AccountRepository;
import com.example.hotelAPI.repository.CheckInRepository;
import com.example.hotelAPI.service.CheckInService;
import com.example.hotelAPI.service.EmployeeService;
import com.example.hotelAPI.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRepository checkInRepository;
    private final AccountRepository accountRepository; // Añadido para gestionar cuentas y pagos
    private final CheckInMapper checkInMapper;

    private final BookingServiceImpl bookingService;
    private final RoomService roomService;
    private final EmployeeService employeeService;
    private final UserServiceImpl userService;

    // 1. Find Check-In by ID
    @Override
    public CheckInEntity getEntityById(Long id) {
        return checkInRepository.findById(id)
                .orElseThrow(() -> new CheckInNotFoundException("Check-in registration not found"));
    }

    @Override
    public CheckInDTOResponse findById(Long id) {
        return checkInMapper.toDto(getEntityById(id));
    }

    // 2. List all check-ins or only ACTIVE ones
    @Override
    public List<CheckInDTOResponse> list(Boolean active) {
        List<CheckInEntity> checkIns;
        if (active == null) {
            checkIns = checkInRepository.findAll();
        } else {
            checkIns = checkInRepository.findByActiveTrue();
        }
        return checkIns.stream()
                .map(checkInMapper::toDto)
                .toList();
    }

    // 6. Other searches
    @Override
    public List<CheckInDTOResponse> findByState(CheckInState state) {
        return checkInRepository.findByCheckInState(state).stream()
                .map(checkInMapper::toDto)
                .toList();
    }

    @Override
    public List<CheckInDTOResponse> checkInByLastName(String lastName) {
        List<CheckInEntity> checkIns = checkInRepository.findAll();
        return checkIns.stream()
                .filter(c -> c.getUserEntity().getSurname().toLowerCase().contains(lastName.toLowerCase()))
                .map(checkInMapper::toDto)
                .toList();
    }

    @Override
    public List<CheckInDTOResponse> checkInByDni(String dni) {
        List<CheckInEntity> checkIns = checkInRepository.findAll();
        return checkIns.stream()
                .filter(c -> c.getUserEntity().getDni().contains(dni))
                .map(checkInMapper::toDto)
                .toList();
    }

    @Override
    public List<CheckInDTOResponse> historyCheckInsByRoom(Integer roomNumber) {
        List<CheckInEntity> checkIns = checkInRepository.findAll();
        return checkIns.stream()
                .filter(c -> c.getBookingEntity().getRoom().getNumber().equals(roomNumber))
                .sorted(Comparator.comparing((CheckInEntity c) -> c.getBookingEntity().getCheckOut()).reversed())
                .map(checkInMapper::toDto)
                .toList();
    }

    @Override
    public List<CheckInDTOResponse> checkOutsOfTheDay() {
        List<CheckInEntity> checkIns = checkInRepository.findByActiveTrue().stream()
                .filter(c -> c.getBookingEntity().getCheckOut().equals(LocalDate.now()))
                .toList();

        return checkIns.stream()
                .map(checkInMapper::toDto)
                .toList();
    }

    // 7. CHECK-IN Methods
    @Transactional
    @Override
    public CheckInDTOResponse checkIn(CheckInDTORequest request) {
        BookingEntity booking = bookingService.findEntityById(request.getBookingId());
        RoomEntity room = booking.getRoom();

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String employeeUsername = "";
        if(principal instanceof UserDetails){
            employeeUsername = ((UserDetails) principal).getUsername();
        }

        UserDtoResponse userDtoResponse = userService.findByUsername(employeeUsername);
        EmployeeEntity employee = employeeService.findEntityById(userDtoResponse.getId());

        if (employee.getUser() != null && !employee.getUser().isEnabled()) {
            throw new DisabledUserException("A disabled employee cannot generate a check-in.");
        }

        UserEntity guest = userService.findEntityById(request.getUserId());
        validations(booking, room, employee, guest);

        CheckInEntity checkIn = checkInMapper.toEntity(request);
        checkIn.setBookingEntity(booking);
        checkIn.setCheckInState(CheckInState.CURRENTLY_ACTIVE);
        checkIn.setUserEntity(guest);
        checkIn.setEmployeeEntity(employee);
        checkIn.setTotal(request.getTotal()); // O booking.getTotalPrice() según prefieras
        checkIn.setActive(true);

        CheckInEntity savedCheckIn = checkInRepository.save(checkIn);

        // Crear automáticamente la cuenta contable asociada al Check-In
        AccountEntity account = AccountEntity.builder()
                .checkIn(savedCheckIn)
                .totalAmount(savedCheckIn.getTotal())
                .paidAmount(0.0)
                .isPaid(false)
                .build();
        accountRepository.save(account);

        room.setState(RoomState.OCCUPIED);
        roomService.updateRoom(room);

        booking.setState(BookingState.CHECKED_IN);
        bookingService.update(booking);

        return checkInMapper.toDto(savedCheckIn);
    }

    private void validations(BookingEntity booking,
                             RoomEntity room,
                             EmployeeEntity employee,
                             UserEntity guest) {
        if (booking == null) {
            throw new BookingNotFoundException("Booking with that ID does not exist");
        }
        if (guest == null) {
            throw new UserNotFoundException("Guest with that ID does not exist");
        }
        if (employee == null) {
            throw new UserNotFoundException("Employee with that ID does not exist");
        }
        if (!employee.getUser().isEnabled()) {
            throw new DisabledUserException("A disabled employee cannot process a check-in");
        }
        if (!booking.getActive()) {
            throw new BookingStateConflictException("Invalid check-in due to booking status: " + booking.getState());
        }
        if (booking.getState().equals(BookingState.CHECKED_IN)) {
            throw new BookingStateConflictException("This booking has already been checked in");
        }
        if (room.getState().equals(RoomState.MAINTENANCE) || room.getState().equals(RoomState.OCCUPIED)) {
            throw new InvalidCheckInException("Check-in impossible because room " + room.getNumber() + " is " + room.getState());
        }

        LocalDate today = LocalDate.now();
        if (booking.getCheckIn().isBefore(today) || booking.getCheckIn().isAfter(today)) {
            throw new InvalidCheckInException("Check-in is only allowed on the exact day of the reservation's start date.");
        }
    }

    // 8. Interrupt Stay Methods
    @Transactional
    @Override
    public CheckInDTOResponse interruptStay(Long id, String reason) {
        CheckInEntity checkIn = getEntityById(id);
        BookingEntity booking = checkIn.getBookingEntity();
        RoomEntity room = booking.getRoom();

        validationsInterruption(checkIn);

        checkIn.setCheckInState(CheckInState.INTERRUPTED);
        checkIn.setActive(false);
        checkInRepository.save(checkIn);

        booking.setState(BookingState.PENDING);
        bookingService.cancelBooking(new BookingCancellationDTORequest(booking.getId(), reason));

        room.setState(RoomState.AVAILABLE);
        roomService.updateRoom(room);

        return checkInMapper.toDto(checkIn);
    }

    private void validationsInterruption(CheckInEntity checkIn) {
        AccountEntity account = accountRepository.findByCheckInId(checkIn.getId())
                .orElseThrow(() -> new RuntimeException("Account not found for this check-in"));

        if (checkIn.getBookingEntity().getCheckOut().equals(LocalDate.now())) {
            throw new InvalidDateException("The stay cannot be interrupted because checkout is today");
        }
        if (checkIn.getCheckInState().equals(CheckInState.COMPLETED)) {
            throw new BookingStateConflictException("The stay has already completed");
        }
        if (!Boolean.TRUE.equals(account.getIsPaid())) {
            throw new BookingStateConflictException("The stay must be settled/paid before interrupting");
        }
        if (checkIn.getCheckInState().equals(CheckInState.INTERRUPTED)) {
            throw new BookingStateConflictException("The stay is already interrupted");
        }
    }

    // 9. CHECK-OUT Methods
    @Transactional
    @Override
    public CheckInDTOResponse checkOutStay(Long id) {
        CheckInEntity checkIn = getEntityById(id);
        BookingEntity booking = checkIn.getBookingEntity();
        RoomEntity room = booking.getRoom();

        validationsCheckOut(checkIn);

        checkIn.setCheckInState(CheckInState.COMPLETED);
        checkIn.setActive(false);
        checkInRepository.save(checkIn);

        booking.setState(BookingState.CONCLUDED);
        bookingService.update(booking);

        room.setState(RoomState.AVAILABLE);
        roomService.updateRoom(room);

        return checkInMapper.toDto(checkIn);
    }

    private void validationsCheckOut(CheckInEntity checkIn) {
        AccountEntity account = accountRepository.findByCheckInId(checkIn.getId())
                .orElseThrow(() -> new RuntimeException("Account not found for this check-in"));

        if (checkIn.getBookingEntity().getCheckOut().isAfter(LocalDate.now())) {
            throw new BookingStateConflictException("Checkout cannot be performed early. Use the 'interrupt' option instead");
        }
        if (!Boolean.TRUE.equals(account.getIsPaid())) {
            throw new BookingStateConflictException("The stay must be paid prior to checkout");
        }
    }

    // 10. Pay Stay (Adaptado para verificar la cuenta)
    @Override
    public CheckInDTOResponse payStay(Long id) {
        CheckInEntity checkIn = getEntityById(id);
        AccountEntity account = accountRepository.findByCheckInId(checkIn.getId())
                .orElseThrow(() -> new RuntimeException("Account not found for this check-in"));

        LocalDate checkOutDate = checkIn.getBookingEntity().getCheckOut();

        if (!checkOutDate.equals(LocalDate.now())) {
            if (Boolean.TRUE.equals(account.getIsPaid())) {
                throw new BookingStateConflictException("The stay is already paid");
            }
        }

        // Nota: Los pagos ahora se registran a través de AccountService / addPaymentToAccount.
        // Aquí puedes retornar el checkIn o realizar lógica adicional si lo requieres.
        return checkInMapper.toDto(checkIn);
    }

    // 11. Check if check-ins exist for a room
    @Override
    public boolean existsByCheckInPorHabitacion(Long roomId) {
        return checkInRepository.findAll().stream()
                .anyMatch(c -> c.getBookingEntity().getRoom().getId().equals(roomId));
    }

    // KPIs
    @Override
    public Double occupancyRateByDateRange(LocalDate startDate, LocalDate endDate) {
        Integer roomCount = roomService.roomCount();
        if (roomCount == 0) {
            return 0.0;
        }
        long checkInCountInRange = checkInRepository.findAll().stream()
                .filter(c -> {
                    LocalDate checkInDate = c.getBookingEntity().getCheckIn();
                    LocalDate checkOutDate = c.getBookingEntity().getCheckOut();
                    return (checkInDate.equals(startDate) || checkInDate.isAfter(startDate)) &&
                            (checkOutDate.equals(endDate) || checkOutDate.isBefore(endDate));
                }).count();

        return 100 - ((checkInCountInRange * 100.0) / roomCount);
    }

    @Override
    public Integer activeStaysCount() {
        return checkInRepository.findByCheckInState(CheckInState.CURRENTLY_ACTIVE).size();
    }

    @Override
    public Double todaysCheckInRevenue() {
        return checkInRepository.findAll().stream()
                .filter(c -> c.getBookingEntity().getCheckIn().equals(LocalDate.now()))
                .filter(c -> {
                    AccountEntity acc = accountRepository.findByCheckInId(c.getId()).orElse(null);
                    return acc != null && Boolean.TRUE.equals(acc.getIsPaid());
                })
                .map(CheckInEntity::getTotal)
                .reduce(0.0, Double::sum);
    }

    @Override
    public Map<String, Double> revenueByMonthAndYear(Integer year) {
        if (year == null) {
            throw new InvalidDateException("The year parameter cannot be null");
        }

        int currentYear = LocalDate.now().getYear();
        int baseYear = 2024;

        if (year > currentYear) {
            throw new InvalidDateException("Cannot request revenue for a future year");
        }

        if (year < baseYear) {
            throw new InvalidDateException("The requested year is prior to the hotel operations start year (" + baseYear + ")");
        }

        return checkInRepository.findAll().stream()
                .filter(c -> c.getBookingEntity().getCheckIn().getYear() == year)
                .filter(c -> {
                    AccountEntity acc = accountRepository.findByCheckInId(c.getId()).orElse(null);
                    return acc != null && Boolean.TRUE.equals(acc.getIsPaid());
                })
                .collect(Collectors.groupingBy(
                        c -> c.getBookingEntity().getCheckIn().getMonth().name(),
                        Collectors.summingDouble(CheckInEntity::getTotal)
                ));
    }
}