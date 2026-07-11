package com.example.hotelAPI.model;

import com.example.hotelAPI.enums.Shift;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "employees")
@PrimaryKeyJoinColumn(name = "user_id") // Une la clave primaria de empleados con la de usuarios
public class EmployeeEntity extends UserEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Shift shift;

    @Column(nullable = false)
    private Double salary;
}