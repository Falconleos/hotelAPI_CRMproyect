package com.example.hotelAPI.service;

import com.example.hotelAPI.dto.request.CommentDTORequest;
import com.example.hotelAPI.dto.response.CommentDTOResponse;
import java.util.List;

public interface CommentService {
    CommentDTOResponse createComment(CommentDTORequest request);

    // AGREGA ESTA LÍNEA:
    CommentDTOResponse updateComment(Long id, CommentDTORequest request);

    List<CommentDTOResponse> getCommentsByCheckIn(Long checkInId);
    List<CommentDTOResponse> getAllComments();
    List<CommentDTOResponse> getMyComments();
    void deleteComment(Long id);
}