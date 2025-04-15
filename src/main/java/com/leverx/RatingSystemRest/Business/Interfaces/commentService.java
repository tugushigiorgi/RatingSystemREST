package com.leverx.RatingSystemRest.Business.Interfaces;

import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.addCommentDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface commentService {


    ResponseEntity<String> add(addCommentDto dto);

    ResponseEntity<String> delete(int anonymousId, int commentId);

    ResponseEntity<String> update(CommentUpdateDto dto);

    ResponseEntity<List<UserReviewsDto>> getApprovedReviewsBySellerId(int sellerId);

    List<UserReviewsDto> getNotApprovedReviewsBySellerId(int sellerId) throws Exception;

    List<UserReviewsDto> getAllNotApprovedReviews();

    ResponseEntity<String> approveUserReview(int commentId);

    ResponseEntity<String> declineUserReview(int commentId);

}
