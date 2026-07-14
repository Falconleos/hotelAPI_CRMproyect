package com.example.hotelAPI.model;

import com.example.hotelAPI.enums.Shift;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "employees")
public class EmployeeEntity{

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Shift shift;

    @Column(nullable = false)
    private Double salary;

    @OneToOne(fetch = FetchType.LAZY) // Relación 1 a 1 con carga perezosa
    @MapsId // Comparte la clave primaria con UserEntity (mantiene la eficiencia)
    @JoinColumn(name = "user_id") // Nombre de la columna FK/PK en la tabla employees
    private UserEntity user;
}