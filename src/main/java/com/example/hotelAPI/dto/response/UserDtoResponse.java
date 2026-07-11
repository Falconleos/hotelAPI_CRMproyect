package com.example.hotelAPI.dto.response;

import com.example.hotelAPI.model.RoleEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

public class UserDtoResponse {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

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
    private boolean isAccountNonExpired;

    @Column(nullable = false)
    private boolean isAccountNonLocked;

    @Column(nullable = false)
    private boolean isCredentialsNonExpired;

    @Column(nullable = false)
    private boolean isEnabled;

}
