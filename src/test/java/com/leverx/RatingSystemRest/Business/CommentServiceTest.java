package com.leverx.RatingSystemRest.Business;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.addCommentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;




    @Test
    void getApprovedReviewsBySellerId_WhenSellerDoesNotExist_ShouldThrowException() {
        int sellerId = 1;
        when(userRepository.findById(sellerId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> commentService.getApprovedReviewsBySellerId(sellerId));
    }




    @Test
    void addComment_WhenSellerDoesNotExist_ShouldThrowException() {
        addCommentDto dto = new addCommentDto(5, 1, "Great seller!", 123);
        when(userRepository.findById(dto.getSellerId())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> commentService.add(dto));
    }
}
