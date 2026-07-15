package com.example.hotelAPI.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<RoleEntity> roles;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String surname;

    @Column(nullable = false, unique = true, updatable = false, length = 15)
    private String dni;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(length = 15, unique = true)
    private String phoneNumber;

    @Column(nullable = false, updatable = false)
    private LocalDate birthDay;

    @Column(nullable = false, updatable = false)
    private LocalDate createAt;

    @Column(nullable = false)
    private boolean accountNonExpired;

    @Column(nullable = false)
    private boolean accountNonLocked;

    @Column(nullable = false)
    private boolean credentialsNonExpired;

    @Column(nullable = false)
    private boolean enabled;

    // Dentro de UserEntity.java
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private EmployeeEntity employee;

}


