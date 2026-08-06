package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.ItemDTORequest;
import com.example.hotelAPI.dto.response.ItemDTOResponse;

import java.util.List;

public interface ItemService {
    List<ItemDTOResponse> getAllItems();
    ItemDTOResponse findById(Long id);
    ItemDTOResponse createItem(ItemDTORequest request);
    ItemDTOResponse updateItem(Long id, ItemDTORequest request);
    void deleteItem(Long id);
}