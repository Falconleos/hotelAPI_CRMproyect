package com.example.hotelAPI.security;

import com.example.hotelAPI.model.RoleEntity;
import com.example.hotelAPI.model.UserEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CustomUserDetails(UserEntity userEntity) implements UserDetails {

    private static final String PREFIX = "ROLE_";

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userEntity.getRoles().stream()
                .map(roleEntity -> new SimpleGrantedAuthority(PREFIX + roleEntity.getName().name().toUpperCase())) // <- Usá .name().toUpperCase()
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userEntity.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userEntity.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userEntity.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return userEntity.isEnabled();
    }
}
