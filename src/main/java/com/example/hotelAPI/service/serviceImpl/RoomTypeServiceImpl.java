package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.model.RoomTypeEntity;
import com.example.hotelAPI.repository.RoomTypeRepository;
import com.example.hotelAPI.service.RoomTypeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeEntity> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomTypeEntity getRoomTypeById(Long id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room type not found with ID: " + id));
    }

    @Override
    @Transactional
    public RoomTypeEntity createRoomType(RoomTypeEntity roomType) {
        if (roomTypeRepository.existsByName(roomType.getName())) {
            throw new IllegalArgumentException("A room type with the name '" + roomType.getName() + "' already exists.");
        }
        return roomTypeRepository.save(roomType);
    }

    @Override
    @Transactional
    public RoomTypeEntity updateRoomType(Long id, RoomTypeEntity roomTypeDetails) {
        RoomTypeEntity existingType = getRoomTypeById(id);

        if (!existingType.getName().equalsIgnoreCase(roomTypeDetails.getName()) &&
                roomTypeRepository.existsByName(roomTypeDetails.getName())) {
            throw new IllegalArgumentException("A room type with the name '" + roomTypeDetails.getName() + "' already exists.");
        }

        existingType.setName(roomTypeDetails.getName());
        existingType.setCapacity(roomTypeDetails.getCapacity());
        existingType.setDescription(roomTypeDetails.getDescription());
        existingType.setPricePerNight(roomTypeDetails.getPricePerNight());

        return roomTypeRepository.save(existingType);
    }

    @Override
    @Transactional
    public void deleteRoomType(Long id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Room type not found with ID: " + id);
        }
        roomTypeRepository.deleteById(id);
    }
}
