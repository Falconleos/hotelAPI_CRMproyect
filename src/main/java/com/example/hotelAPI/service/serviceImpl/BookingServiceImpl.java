package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.BookingCancellationDTORequest;
import com.example.hotelAPI.dto.request.BookingDTORequest;
import com.example.hotelAPI.dto.response.BookingCancellationDTOResponse;
import com.example.hotelAPI.dto.response.BookingDTOResponse;
import com.example.hotelAPI.dto.response.RoomDTOResponse;
import com.example.hotelAPI.dto.response.UserDtoResponse;
import com.example.hotelAPI.enums.BookingState;
import com.example.hotelAPI.exceptions.*;
import com.example.hotelAPI.mappers.BookingCancellationMapper;
import com.example.hotelAPI.mappers.BookingMapper;
import com.example.hotelAPI.mappers.RoomMapper;
import com.example.hotelAPI.model.BookingCancellationEntity;
import com.example.hotelAPI.model.BookingEntity;
import com.example.hotelAPI.model.EmployeeEntity;
import com.example.hotelAPI.model.RoomEntity;
import com.example.hotelAPI.repository.BookingCancellationRepository;
import com.example.hotelAPI.repository.BookingRepository;
import com.example.hotelAPI.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingCancellationRepository bookingCancellationRepository;
    private final BookingMapper bookingMapper;

    private final BookingCancellationMapper bookingCancellationMapper;
    private final BookingCancellationService bookingCancellationService;

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    private final UserService userService;
    private final EmployeeService employeeService;

    // 1. Busqueda por ID
    @Override
    @Transactional(readOnly = true)
    public BookingEntity findEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTOResponse findById(Long id) {
        return bookingMapper.toDto(findEntityById(id));
    }

    // 2. Listar reservas activas o inactivas
    @Override
    @Transactional(readOnly = true)
    public List<BookingDTOResponse> getBookings(Boolean active) {
        List<BookingEntity> bookings;
        if (active == null) {
            bookings = bookingRepository.findAll();
        } else {
            bookings = bookingRepository.findByActive(active);
        }
        return bookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    // 3. Crear reserva
    @Override
    @Transactional
    public BookingDTOResponse createBooking(BookingDTORequest request) {
        if (!request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new InvalidDateException("The check-out date must be after the check-in date.");
        }

        RoomEntity room = roomService.findEntityById(request.getRoomId());

        if (room.getType().getCapacity() < request.getGuestCount()) {
            throw new CapacityOutOfRangeException("Room capacity exceeded for the requested guest count.");
        }

        if (!getAvailableRoomsEntities(request.getCheckIn(), request.getCheckOut(), request.getGuestCount()).contains(room)) {
            throw new DisabledRoomException("The selected room is not available for these dates.");
        }

        long days = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        double totalPrice = room.getType().getPricePerNight() * days;

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String employeeUsername = "";

        if(principal instanceof UserDetails){
            employeeUsername = ((UserDetails) principal).getUsername();
        }

        UserDtoResponse userDtoResponse = userService.findByUsername(employeeUsername);
        EmployeeEntity employee = employeeService.findEntityById(userDtoResponse.getId());

        if (employee.getUser() != null && !employee.getUser().isEnabled()) {
            throw new DisabledUserException("A disabled employee cannot generate a booking.");
        }

        BookingEntity booking = bookingMapper.toEntity(request);
        booking.setEmployee(employee);
        booking.setRoom(room);
        booking.setTotalPrice(totalPrice);
        booking.setActive(true);
        booking.setState(BookingState.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        BookingEntity savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    // 3.2. Cancelar una reserva
    @Override
    @Transactional
    public BookingCancellationDTOResponse cancelBooking(BookingCancellationDTORequest request) {
        BookingEntity booking = findEntityById(request.getBookingId());

        if (booking.getState() == BookingState.CHECKED_IN) {
            throw new BookingStateConflictException("The guest has already checked in. You can only interrupt the stay.");
        }
        if (booking.getState() == BookingState.NO_SHOW) {
            throw new BookingStateConflictException("This booking is already inactive due to a No-Show.");
        }
        if (booking.getState() == BookingState.CONCLUDED) {
            throw new BookingStateConflictException("Cannot cancel a concluded booking.");
        }
        if (booking.getState() == BookingState.CANCELLED) {
            throw new BookingStateConflictException("This booking is already cancelled.");
        }

        // Validación optimizada usando el repositorio de cancelaciones
        if (bookingCancellationRepository.existsByBookingId(booking.getId())) {
            throw new BookingStateConflictException("Esta reserva ya cuenta con un registro físico de cancelación en el sistema.");
        }

        // Actualizamos estado de la reserva
        booking.setState(BookingState.CANCELLED);
        booking.setActive(false);
        bookingRepository.save(booking);

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String employeeUsername = "";

        if (principal instanceof UserDetails) {
            employeeUsername = ((UserDetails) principal).getUsername();
        }

        UserDtoResponse userDtoResponse = userService.findByUsername(employeeUsername);
        EmployeeEntity employee = employeeService.findEntityById(userDtoResponse.getId());

        if (employee.getUser() != null && !employee.getUser().isEnabled()) {
            throw new DisabledUserException("A disabled employee cannot generate a booking.");
        }

        BookingCancellationEntity cancellation = BookingCancellationEntity.builder()
                .employee(employee)
                .booking(booking)
                .reason(request.getReason())
                .build();

        BookingCancellationEntity savedCancellation = bookingCancellationService.create(cancellation);
        return bookingCancellationMapper.toDto(savedCancellation);
    }

    // 5. Actualizar reserva genérica en el repositorio
    @Override
    @Transactional
    public void update(BookingEntity booking) {
        bookingRepository.save(booking);
    }

    // 5.2. Confirmar reserva (De PENDING a CONFIRMED)
    @Override
    @Transactional
    public BookingDTOResponse confirmBooking(Long id) {
        BookingEntity booking = findEntityById(id);
        if (booking.getState() != BookingState.PENDING) {
            throw new BookingStateConflictException("Current booking state is: " + booking.getState() + ". To confirm, it must be PENDING.");
        }
        booking.setState(BookingState.CONFIRMED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    // 6. Consultas de disponibilidad y listados especializados
    @Override
    @Transactional(readOnly = true)
    public List<RoomEntity> getAvailableRoomsEntities(LocalDate checkIn, LocalDate checkOut, Integer guestCount) {
        List<BookingEntity> allBookings = bookingRepository.findAll();

        List<Long> occupiedRoomIds = allBookings.stream()
                .filter(b -> Boolean.TRUE.equals(b.getActive()))
                .filter(b -> b.getState() != BookingState.CANCELLED &&
                        b.getState() != BookingState.NO_SHOW &&
                        b.getState() != BookingState.INTERRUPTED &&
                        b.getState() != BookingState.CONCLUDED)
                .filter(b -> checkIn.isBefore(b.getCheckOut()) && checkOut.isAfter(b.getCheckIn()))
                .map(b -> b.getRoom().getId())
                .distinct()
                .toList();

        return roomService.findAll().stream()
                .filter(r -> r.getType().getCapacity() >= guestCount)
                .filter(r -> !occupiedRoomIds.contains(r.getId()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTOResponse> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, Integer guestCount) {
        List<RoomEntity> availableRooms = getAvailableRoomsEntities(checkIn, checkOut, guestCount);
        return availableRooms.stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    // 6.2. Check-Ins programados para hoy
    @Override
    @Transactional(readOnly = true)
    public List<BookingDTOResponse> getCheckInsOfToday() {
        return bookingRepository.findByActive(true).stream()
                .filter(b -> b.getCheckIn().equals(LocalDate.now()))
                .map(bookingMapper::toDto)
                .toList();
    }

    // 6.3. Reservas activas PENDING a X días del check-in
    @Override
    @Transactional(readOnly = true)
    public List<BookingDTOResponse> getBookingsToConfirmInDays(Integer days) {
        return bookingRepository.findByActive(true).stream()
                .filter(b -> b.getState() == BookingState.PENDING)
                .filter(b -> LocalDate.now().plusDays(days).isEqual(b.getCheckIn()))
                .map(bookingMapper::toDto)
                .toList();
    }

    // 7. Procesamiento automatizado de Ausencias (No-Show)
    @Override
    @Transactional
    public void processNoShowBookings() {
        LocalDate today = LocalDate.now();
        List<BookingEntity> noShows = bookingRepository.findByActive(true).stream()
                .filter(b -> b.getCheckIn().isBefore(today))
                .filter(b -> b.getState() == BookingState.PENDING || b.getState() == BookingState.CONFIRMED)
                .toList();

        for (BookingEntity booking : noShows) {
            booking.setState(BookingState.NO_SHOW);
            booking.setActive(false);
            bookingRepository.save(booking);
        }
    }

    // 7.2. Validar si existen reservas asociadas a una habitación física
    @Override
    @Transactional(readOnly = true)
    public boolean existsByRoomId(Long roomId) {
        return bookingRepository.existsByRoom_Id(roomId);
    }
}