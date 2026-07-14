package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.ChangePasswordDtoRequest;
import com.example.hotelAPI.dto.request.ResetPasswordDtoRequest;
import com.example.hotelAPI.dto.request.UserDtoRequest;
import com.example.hotelAPI.dto.request.UserLoginDtoRequest;
import com.example.hotelAPI.dto.response.AuthSuccessDtoResponse;
import com.example.hotelAPI.dto.response.AuthTokenResponse;
import com.example.hotelAPI.dto.response.RefreshTokenDtoResponse;
import com.example.hotelAPI.enums.Role;
import com.example.hotelAPI.jwt.JwtService;
import com.example.hotelAPI.mappers.UserMapper;
import com.example.hotelAPI.model.PasswordResetTokenEntity;
import com.example.hotelAPI.model.RefreshTokenEntity;
import com.example.hotelAPI.model.RoleEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.PasswordResetTokenRepository;
import com.example.hotelAPI.repository.RefreshTokenRepository;
import com.example.hotelAPI.repository.RoleRepository;
import com.example.hotelAPI.repository.UserRepository;
import com.example.hotelAPI.security.CustomUserDetails;
import com.example.hotelAPI.security.UserDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
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
    private final RefreshTokenService refreshTokenService;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public AuthSuccessDtoResponse register(UserDtoRequest request){

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
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.createRefreshToken(userEntity.getUsername());
            refreshTokenRepository.save(refreshTokenEntity);

        ResponseCookie cookie = cookieService.createRefreshTokenCookie(refreshTokenEntity.getToken());

        AuthTokenResponse authTokenResponse = new AuthTokenResponse(token);

        return new AuthSuccessDtoResponse(authTokenResponse,cookie);
    }

    public AuthSuccessDtoResponse login(UserLoginDtoRequest request){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtService.generateToken(userDetails);
        RefreshTokenEntity refreshTokenEntity = refreshTokenService.createRefreshToken(userDetails.getUsername());
            refreshTokenRepository.save(refreshTokenEntity);

        ResponseCookie cookie = cookieService.createRefreshTokenCookie(refreshTokenEntity.getToken());

        AuthTokenResponse authTokenResponse = new AuthTokenResponse(token);

        return new AuthSuccessDtoResponse(authTokenResponse,cookie);
    }

    public RefreshTokenDtoResponse refreshAccessToken(String refreshToken){

        return refreshTokenRepository.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUserEntity)
                .map(userEntity -> {
                    String newAccessToken = jwtService.generateToken(new CustomUserDetails(userEntity));
                    return new RefreshTokenDtoResponse(newAccessToken);
                }).orElseThrow(()->new RuntimeException("the refresh token does not exist on database"));

    }

    public ResponseCookie logout(){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if(username!=null && !username.equals("anonymousUser")){

            UserEntity userEntity = userRepository.findByUsername(username)
                            .orElseThrow(()->new UsernameNotFoundException("incorect username"));

            refreshTokenRepository.deleteByUserEntity(userEntity);
        }
        return cookieService.cleanRefreshTokenCookie();
    }

    public void forgotPassword(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return;
        }

        UserEntity user = userOptional.get();

        passwordResetTokenRepository.deleteByUserEntity(user);

        String token = java.util.UUID.randomUUID().toString();

        PasswordResetTokenEntity resetTokenEntity = PasswordResetTokenEntity.builder()
                .token(token)
                .userEntity(user)
                .expiryDate(java.time.LocalDateTime.now().plusMinutes(15))
                .build();

        passwordResetTokenRepository.save(resetTokenEntity);

        //enviar email // a completar mediante consumo de API externa
        System.out.println("Enlace enviado a " + email + ": https://tuapp.com/reset-password?token=" + token);
    }

    @Transactional
    public void resetPassword(ResetPasswordDtoRequest request) {

        PasswordResetTokenEntity resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("invalid or inexistent recuperation token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken); // Limpieza opcional
            throw new RuntimeException("recuperation token is expired");
        }

        UserEntity user = resetToken.getUserEntity();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    public void changePassword(ChangePasswordDtoRequest request) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        if (username == null || username.equals("anonymousUser")) {
            throw new RuntimeException("no authenticated user");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}
