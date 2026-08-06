package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.ItemDTORequest;
import com.example.hotelAPI.dto.response.ItemDTOResponse;
import com.example.hotelAPI.mappers.ItemMapper; // Ajusta según el nombre de tu paquete de mappers
import com.example.hotelAPI.model.ItemEntity;
import com.example.hotelAPI.repository.ItemRepository;
import com.example.hotelAPI.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTOResponse> getAllItems() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTOResponse findById(Long id) {
        ItemEntity entity = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado con ID: " + id));
        return itemMapper.toDto(entity);
    }

    @Override
    @Transactional
    public ItemDTOResponse createItem(ItemDTORequest request) {
        ItemEntity entity = itemMapper.toEntity(request);

        // Cálculo automático del subtotal
        double subtotal = request.getQuantity() * request.getUnitPrice();
        entity.setSubtotal(subtotal);

        ItemEntity savedEntity = itemRepository.save(entity);
        return itemMapper.toDto(savedEntity);
    }

    @Override
    @Transactional
    public ItemDTOResponse updateItem(Long id, ItemDTORequest request) {
        ItemEntity existingEntity = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item no encontrado para actualizar con ID: " + id));

        existingEntity.setDescription(request.getDescription());
        existingEntity.setQuantity(request.getQuantity());
        existingEntity.setUnitPrice(request.getUnitPrice());

        // Recalcular subtotal
        double subtotal = request.getQuantity() * request.getUnitPrice();
        existingEntity.setSubtotal(subtotal);

        ItemEntity updatedEntity = itemRepository.save(existingEntity);
        return itemMapper.toDto(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar, item no encontrado con ID: " + id);
        }
        itemRepository.deleteById(id);
    }
}