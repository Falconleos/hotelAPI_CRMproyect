package com.example.hotelAPI.config;

import com.example.hotelAPI.enums.Role;
import com.example.hotelAPI.model.RoleEntity;
import com.example.hotelAPI.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitilizer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Recorremos los valores de tu Enum
        for (Role role : Role.values()) {
            // Si no existe, lo creamos
            if (roleRepository.findByName(role).isEmpty()) {
                roleRepository.save(
                        RoleEntity.builder()
                        .name(role)
                        .build());
            }
        }
    }

}