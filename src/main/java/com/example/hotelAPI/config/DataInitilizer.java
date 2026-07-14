package com.example.hotelAPI.config;

import com.example.hotelAPI.enums.Role;
import com.example.hotelAPI.model.RoleEntity;
import com.example.hotelAPI.model.RoomTypeEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.RoleRepository;
import com.example.hotelAPI.repository.RoomTypeRepository;
import com.example.hotelAPI.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitilizer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final PasswordEncoder passwordEncoder;

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

        // 2. Crear Administrador por defecto si no existe ninguno
        String adminUsername = "admin";
        if (userRepository.findByUsername(adminUsername).isEmpty()) { // O usa findByEmail si lo prefieres

            // Buscamos el rol ADMIN que acabamos de asegurar que existe
            RoleEntity adminRole = roleRepository.findByName(Role.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: ADMIN role not found during initialization."));

            UserEntity defaultAdmin = UserEntity.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode("admin123")) // Su contraseña inicial encriptada
                    .name("Admin")
                    .surname("System")
                    .dni("00000000") // DNI genérico para evitar colisiones
                    .email("admin@hotel.com")
                    .phoneNumber("123456789")
                    .birthDay( LocalDate.of(1990, 1, 1))
                    .createAt(LocalDate.now())
                    .roles(Set.of(adminRole)) // Asignamos el rol ADMIN
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .build();

            userRepository.save(defaultAdmin);
            System.out.println(">> Default admin user successfully created (Username: admin / Password: admin123) <<");
        }

        // 3. Crear RoomType por defecto ("Basic") si no existe
        String defaultRoomTypeName = "Basic";
        if (!roomTypeRepository.existsByName(defaultRoomTypeName)) {
            RoomTypeEntity defaultType = RoomTypeEntity.builder()
                    .name(defaultRoomTypeName)
                    .capacity(1) // Usamos 1 por la validación @Positive de tu modelo
                    .description("Categoría temporal por defecto")
                    .pricePerNight(0.0)
                    .build();

            roomTypeRepository.save(defaultType);
            System.out.println(">> Default RoomType 'Basic' successfully created <<");
        }

    }

}