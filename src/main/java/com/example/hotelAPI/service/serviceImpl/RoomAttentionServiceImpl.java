package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.RoomAttentionDTORequest;
import com.example.hotelAPI.dto.response.RoomAttentionDTOResponse;
import com.example.hotelAPI.model.CheckInEntity;
import com.example.hotelAPI.model.ItemEntity;
import com.example.hotelAPI.model.RoomAttentionEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.CheckInRepository;
import com.example.hotelAPI.repository.ItemRepository;
import com.example.hotelAPI.repository.RoomAttentionRepository;
import com.example.hotelAPI.repository.UserRepository;
import com.example.hotelAPI.service.AccountService;
import com.example.hotelAPI.service.RoomAttentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomAttentionServiceImpl implements RoomAttentionService {

    private final RoomAttentionRepository roomAttentionRepository;
    private final CheckInRepository checkInRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;

    @Override
    @Transactional
    public RoomAttentionDTOResponse addAttention(RoomAttentionDTORequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity employee = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated employee not found"));

        CheckInEntity checkIn = checkInRepository.findById(request.getCheckInId())
                .orElseThrow(() -> new RuntimeException("Check-in not found"));

        ItemEntity item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item or service not found"));

        // Lógica de stock: Si NO es un servicio, descontamos stock y validamos disponibilidad
        if (!Boolean.TRUE.equals(item.getIsService())) {
            if (item.getQuantity() < request.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para el ítem: " + item.getDescription() + ". Stock disponible: " + item.getQuantity());
            }
            item.setQuantity(item.getQuantity() - request.getQuantity());
            itemRepository.save(item);
        }

        RoomAttentionEntity attention = RoomAttentionEntity.builder()
                .checkIn(checkIn)
                .item(item)
                .quantity(request.getQuantity())
                .unitPrice(item.getUnitPrice())
                .employee(employee)
                .build();

        RoomAttentionEntity saved = roomAttentionRepository.save(attention);

        // Sumamos el subtotal específicamente como servicio/consumo extra (no a la base)
        double totalCharge = saved.getSubtotal().doubleValue();
        accountService.addChargeToAccount(checkIn.getId(), totalCharge);

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void removeAttention(Long id) {
        RoomAttentionEntity attention = roomAttentionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room attention not found"));

        ItemEntity item = attention.getItem();

        // Si NO es un servicio, devolvemos la cantidad al stock al eliminar la atención
        if (!Boolean.TRUE.equals(item.getIsService())) {
            item.setQuantity(item.getQuantity() + attention.getQuantity());
            itemRepository.save(item);
        }

        // Restamos el subtotal del consumo eliminado de los servicios de la cuenta
        double totalCharge = attention.getSubtotal().doubleValue();
        accountService.subtractChargeFromAccount(attention.getCheckIn().getId(), totalCharge);

        roomAttentionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAttentionDTOResponse> getAttentionsByCheckIn(Long checkInId) {
        return roomAttentionRepository.findByCheckInId(checkInId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private RoomAttentionDTOResponse mapToDto(RoomAttentionEntity entity) {
        return RoomAttentionDTOResponse.builder()
                .id(entity.getId())
                .checkInId(entity.getCheckIn().getId())
                .itemId(entity.getItem().getId())
                .itemDescription(entity.getItem().getDescription())
                .isService(entity.getItem().getIsService())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
                .createdAt(entity.getCreatedAt())
                .employeeUsername(entity.getEmployee().getUsername())
                .build();
    }
}