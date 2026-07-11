package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.request.UserLoginDtoRequest;
import com.example.hotelAPI.dto.response.AuthTokenResponse;
import com.example.hotelAPI.enums.Role;
import com.example.hotelAPI.jwt.JwtService;
import com.example.hotelAPI.mappers.UserMapper;
import com.example.hotelAPI.model.RoleEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.RoleRepository;
import com.example.hotelAPI.repository.UserRepository;
import com.example.hotelAPI.security.CustomUserDetails;
import com.example.hotelAPI.security.UserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthTokenResponse register(UserDtoRequest request){

        RoleEntity guestRole = roleRepository.findByName(Role.GUEST)
                .orElseThrow(() -> new RuntimeException("no rol named GUEST"));

        UserEntity userEntity = userMapper.toEntity(request);
            userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
            userEntity.setRoles(Set.of(guestRole));
            userEntity.setCreateAt(LocalDate.now());
            userEntity.setAccountNonExpired(true);
            userEntity.setAccountNonLocked(true);
            userEntity.setCredentialsNonExpired(true);
            userEntity.setEnabled(true);

        userRepository.save(userEntity);

        String token = jwtService.generateToken(new CustomUserDetails(userEntity));

        return new AuthTokenResponse(token);
    }

    public AuthTokenResponse login(UserLoginDtoRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtService.generateToken(userDetails);

        return new AuthTokenResponse(token);
    }

}
