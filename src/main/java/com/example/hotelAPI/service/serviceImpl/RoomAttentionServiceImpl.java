package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.RoomAttentionDTORequest;
import com.example.hotelAPI.dto.response.RoomAttentionDTOResponse;
import com.example.hotelAPI.exceptions.CheckInNotFoundException;
import com.example.hotelAPI.mappers.ItemMapper;
import com.example.hotelAPI.mappers.RoomAttentionMapper;
import com.example.hotelAPI.model.AccountEntity;
import com.example.hotelAPI.model.CheckInEntity;
import com.example.hotelAPI.model.ItemEntity;
import com.example.hotelAPI.model.RoomAttentionEntity;
import com.example.hotelAPI.repository.AccountRepository;
import com.example.hotelAPI.repository.CheckInRepository;
import com.example.hotelAPI.repository.RoomAttentionRepository;
import com.example.hotelAPI.service.RoomAttentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomAttentionServiceImpl implements RoomAttentionService{

    private final RoomAttentionRepository roomAttentionRepository;
    private final CheckInRepository checkInRepository;
    private final RoomAttentionMapper roomAttentionMapper;
    private final ItemMapper itemMapper;
    private final AccountRepository accountRepository;

    @Override
    @Transactional // <-- 2. Asegurar que sea transaccional para guardar cambios en cascada/cuenta
    public RoomAttentionDTOResponse createRoomAttention(RoomAttentionDTORequest request) {
        CheckInEntity checkIn = checkInRepository.findById(request.getCheckInId())
                .orElseThrow(() -> new CheckInNotFoundException("Check-in not found with ID: " + request.getCheckInId()));

        RoomAttentionEntity roomAttention = RoomAttentionEntity.builder()
                .checkIn(checkIn)
                .adjustment(request.getAdjustment() != null ? request.getAdjustment() : 0.0)
                .paid(false)
                .build();

        List<ItemEntity> items = request.getItems().stream().map(itemReq -> {
            ItemEntity item = itemMapper.toEntity(itemReq);
            item.setRoomAttentionEntity(roomAttention);
            Double subtotal = item.getUnitPrice() * item.getQuantity();
            item.setSubtotal(subtotal);
            return item;
        }).collect(Collectors.toList());

        roomAttention.setItems(items);
        roomAttention.calculateTotal(); // Calcula el total del Room Attention

        // 3. Buscar la cuenta asociada al Check-In
        AccountEntity account = accountRepository.findByCheckInId(checkIn.getId())
                .orElseThrow(() -> new RuntimeException("Account not found for Check-In ID: " + checkIn.getId()));

        // 4. Sumar el total del consumo a la cuenta principal
        account.addRoomServiceCharge(roomAttention.getTotal());

        // Guardar la cuenta actualizada (o delegar la persistencia)
        accountRepository.save(account);

        // Guardar el Room Attention
        RoomAttentionEntity saved = roomAttentionRepository.save(roomAttention);

        return roomAttentionMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomAttentionDTOResponse> getRoomAttentionByCheckIn(Long checkInId) {
        return roomAttentionRepository.findByCheckInId(checkInId).stream()
                .map(roomAttentionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoomAttentionDTOResponse payRoomAttention(Long id) {
        RoomAttentionEntity roomService = roomAttentionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room service not found with ID: " + id));

        if (Boolean.TRUE.equals(roomService.getPaid())) {
            throw new IllegalStateException("This room service is already paid.");
        }

        roomService.setPaid(true);
        return roomAttentionMapper.toDto(roomAttentionRepository.save(roomService));
    }

    @Transactional
    @Override
    public void deleteRoomAttention(Long id) {
        RoomAttentionEntity roomService = roomAttentionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room service not found with ID: " + id));
        if (Boolean.TRUE.equals(roomService.getPaid())) {
            throw new IllegalStateException("Cannot delete a room service that is already paid.");
        }
        roomAttentionRepository.delete(roomService);
    }


}
