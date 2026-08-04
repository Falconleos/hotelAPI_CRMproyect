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

        String username;

        if(SecurityContextHolder.getContext().getAuthentication()!=null){
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }else{
            throw new InvalidNameException("no username detected");
        }

        // 1. Buscar usuario actual
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Buscar la estadía (CheckIn)
        CheckInEntity checkIn = checkInRepository.findById(request.getCheckInId())
                .orElseThrow(() -> new CheckInNotFoundException("Check-in not found with ID: " + request.getCheckInId()));

        // 3. Validar que el usuario sea el que formó parte de la estadía
        if (!checkIn.getUserEntity().getId().equals(user.getId())) {
            throw new UnauthorizedCommentException("El usuario no formó parte de esta estadía y no puede comentarla.");
        }

        // 5. Mapear y guardar
        CommentEntity comment = commentMapper.toEntity(request);
        comment.setUser(user);
        comment.setCheckIn(checkIn);

        CommentEntity saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTOResponse> getCommentsByCheckIn(Long checkInId) {
        return commentRepository.findByCheckInId(checkInId).stream()
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
}