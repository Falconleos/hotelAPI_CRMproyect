package com.example.hotelAPI.service.impl; // O ajusta el paquete según tu estructura de carpetas (ej. com.example.hotelAPI.service)

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

    @Override
    @Transactional
    public RoomAttentionDTOResponse addAttention(RoomAttentionDTORequest request) {
        // Obtener el username del empleado logueado desde el contexto de seguridad de Spring
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity employee = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Authenticated employee not found"));

        CheckInEntity checkIn = checkInRepository.findById(request.getCheckInId())
                .orElseThrow(() -> new RuntimeException("Check-in not found"));

        ItemEntity item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item or service not found"));

        RoomAttentionEntity attention = RoomAttentionEntity.builder()
                .checkIn(checkIn)
                .item(item)
                .quantity(request.getQuantity())
                .unitPrice(item.getUnitPrice()) // Congela el precio actual del catálogo
                .employee(employee)             // Asigna el empleado logueado automáticamente
                .build();

        RoomAttentionEntity saved = roomAttentionRepository.save(attention);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void removeAttention(Long id) {
        if (!roomAttentionRepository.existsById(id)) {
            throw new RuntimeException("Room attention not found");
        }
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