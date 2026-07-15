package com.example.hotelAPI.config;

import com.example.hotelAPI.dto.request.EmployeeDTORequest;
import com.example.hotelAPI.enums.Role;
import com.example.hotelAPI.enums.Shift;
import com.example.hotelAPI.model.EmployeeEntity;
import com.example.hotelAPI.model.RoleEntity;
import com.example.hotelAPI.model.RoomTypeEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.EmployeeRepository;
import com.example.hotelAPI.repository.RoleRepository;
import com.example.hotelAPI.repository.RoomTypeRepository;
import com.example.hotelAPI.repository.UserRepository;
import com.example.hotelAPI.service.EmployeeService;
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
    private final EmployeeRepository employeeRepository;
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
        if (userRepository.findByUsername(adminUsername).isEmpty()) {

            RoleEntity adminRole = roleRepository.findByName(Role.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: ADMIN role not found during initialization."));

            UserEntity defaultAdmin = UserEntity.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode("admin123"))
                    .name("Admin")
                    .surname("System")
                    .dni("00000000")
                    .email("admin@hotel.com")
                    .phoneNumber("123456789")
                    .birthDay(LocalDate.of(1990, 1, 1))
                    .createAt(LocalDate.now())
                    .roles(Set.of(adminRole))
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .build();

            // 🌟 CLAVE: Capturamos la entidad persistida devuelta por el save()
            UserEntity savedAdmin = userRepository.save(defaultAdmin);
            System.out.println(">> Default admin user successfully created (ID: " + savedAdmin.getId() + ") <<");

            EmployeeEntity employeeEntity = EmployeeEntity.builder()
                    .user(savedAdmin)
                    .shift(Shift.MORNING)
                    .salary(0.0)
                    .build();

            savedAdmin.setEmployee(employeeEntity);

            employeeRepository.save(employeeEntity);
            System.out.println(">> Default admin employee successfully created (ID: " + savedAdmin.getId() + ") <<");


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