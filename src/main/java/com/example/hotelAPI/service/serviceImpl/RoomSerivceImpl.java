package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.RoomDTORequest;
import com.example.hotelAPI.dto.request.RoomUpdateDTO;
import com.example.hotelAPI.dto.response.RoomDTOResponse;
import com.example.hotelAPI.enums.RoomState;
import com.example.hotelAPI.mappers.RoomMapper;
import com.example.hotelAPI.model.RoomEntity;
import com.example.hotelAPI.model.RoomTypeEntity;
import com.example.hotelAPI.repository.RoomRepository;
import com.example.hotelAPI.repository.RoomTypeRepository;
import com.example.hotelAPI.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomSerivceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    private final RoomMapper roomMapper;

    @Override
    @Transactional(readOnly = true)
    public RoomEntity findEntityById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDTOResponse findById(Long id) {
        RoomEntity room = findEntityById(id);
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomEntity> findAll() {
        return roomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTOResponse> getAll() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public RoomDTOResponse save(RoomDTORequest dto) {
        // 1. Validación de número único
        if (roomRepository.findByNumber(dto.getNumber()).isPresent()) {
            throw new RuntimeException("A room with number " + dto.getNumber() + " already exists.");
        }

        // 2. Buscar y asociar el RoomTypeEntity obligatorio
        RoomTypeEntity roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new RuntimeException("Room Type not found with ID: " + dto.getRoomTypeId()));

        // 3. Mapeo y seteo de estado inicial
        RoomEntity room = roomMapper.toEntity(dto);
        room.setType(roomType);
        room.setState(RoomState.AVAILABLE); // Inicializa disponible por defecto

        RoomEntity savedRoom = roomRepository.save(room);
        return roomMapper.toResponse(savedRoom);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RoomEntity room = findEntityById(id);

        if (room.getState() == RoomState.OCCUPIED) {
            throw new RuntimeException("Cannot delete an occupied room.");
        }

        /* Validación de reservas pendientes adaptada al nuevo modelo
        if (bookingRepository.existsByRoomEntity_Id(id)) {
            throw new RoomInUseException("Cannot delete the room: there are bookings associated with it.");
        }*/

        roomRepository.delete(room);
    }

    @Override
    @Transactional
    public RoomDTOResponse update(Long id, RoomUpdateDTO dto) {
        RoomEntity room = findEntityById(id);

        // Si el DTO incluye un cambio de tipo de habitación, lo actualizamos
        if (dto.getRoomTypeId() != null && !dto.getRoomTypeId().equals(room.getType().getId())) {
            RoomTypeEntity newType = roomTypeRepository.findById(dto.getRoomTypeId())
                    .orElseThrow(() -> new RuntimeException("Room Type not found with ID: " + dto.getRoomTypeId()));
            room.setType(newType);
        }

        roomMapper.updateRoomFromDto(dto, room);
        RoomEntity updatedRoom = roomRepository.save(room);

        return roomMapper.toResponse(updatedRoom);
    }

    @Override
    @Transactional
    public void updateRoom(RoomEntity roomEntity) {
        roomRepository.save(roomEntity);
    }

    @Override
    public RoomDTOResponse doMaintenance(Long id) {
        RoomEntity room = findEntityById(id);

        if (room.getState() == RoomState.MAINTENANCE) {
            throw new RuntimeException("The room is already under maintenance.");
        }

        room.setState(RoomState.MAINTENANCE);
        RoomEntity updatedRoom = roomRepository.save(room);

        return roomMapper.toResponse(updatedRoom);
    }

    @Override
    @Transactional
    public List<RoomDTOResponse> availableRooms() {
        return roomRepository.findByState(RoomState.AVAILABLE)
                .stream()
                .map(roomMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomEntity> findByRoomState(RoomState roomState) {
        return roomRepository.findByState(roomState);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer roomCount() {
        return (int) roomRepository.count();
    }
}
