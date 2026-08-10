package com.example.hotelAPI.service.serviceImpl;

import com.example.hotelAPI.dto.request.CommentDTORequest;
import com.example.hotelAPI.dto.response.CommentDTOResponse;
import com.example.hotelAPI.exceptions.CheckInNotFoundException;
import com.example.hotelAPI.exceptions.InvalidNameException;
import com.example.hotelAPI.exceptions.UnauthorizedCommentException;
import com.example.hotelAPI.mappers.CommentMapper;
import com.example.hotelAPI.model.CheckInEntity;
import com.example.hotelAPI.model.CommentEntity;
import com.example.hotelAPI.model.UserEntity;
import com.example.hotelAPI.repository.CheckInRepository;
import com.example.hotelAPI.repository.CommentRepository;
import com.example.hotelAPI.repository.UserRepository;
import com.example.hotelAPI.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public CommentDTOResponse createComment(CommentDTORequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CheckInEntity checkIn = checkInRepository.findById(request.getCheckInId())
                .orElseThrow(() -> new CheckInNotFoundException("Check-in not found with ID: " + request.getCheckInId()));

        // NUEVA LÓGICA:
        // 1. Verificamos si el usuario tiene rol de ADMIN
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equalsIgnoreCase("ADMIN"));

        // 2. Solo lanzamos la excepción si NO es el dueño Y TAMPOCO es administrador
        if (!checkIn.getUserEntity().getId().equals(user.getId()) && !isAdmin) {
            throw new UnauthorizedCommentException("El usuario no formó parte de esta estadía y no puede comentarla.");
        }

        // El resto del código sigue igual
        CommentEntity comment = commentMapper.toEntity(request);
        comment.setUser(user);
        comment.setCheckIn(checkIn);

        CommentEntity saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTOResponse> getMyComments() {
        String username;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
            throw new InvalidNameException("no username detected");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return commentRepository.findByUserId(user.getId()).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTOResponse> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentRepository.delete(comment);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CommentDTOResponse> getCommentsByCheckIn(Long checkInId) {
        // Validamos que exista la estadía o simplemente buscamos por checkInId en el repositorio
        return commentRepository.findByCheckInId(checkInId).stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDTOResponse updateComment(Long id, CommentDTORequest request) {
        // 1. Buscar el comentario existente
        CommentEntity comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + id));

        // 2. Obtener el usuario actual logueado
        String username;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
            throw new RuntimeException("No username detected");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Validar permisos (Dueño del comentario o Admin)
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equalsIgnoreCase("ADMIN"));

        boolean isOwner = comment.getUser().getId().equals(user.getId());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("No tienes permisos para modificar este comentario.");
        }

        // 4. Actualizar los campos permitidos
        comment.setContent(request.getContent());
        comment.setRating(request.getRating());

        // 5. Guardar y retornar
        CommentEntity updatedComment = commentRepository.save(comment);
        return commentMapper.toDto(updatedComment);
    }

}