package com.example.hotelAPI.model;

import com.example.hotelAPI.enums.CheckInState;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "check_ins")
public class CheckInEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "booking_id")
    private BookingEntity bookingEntity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employeeEntity;

    private CheckInState checkInState;
    private Double total;
    private Boolean active;

}
