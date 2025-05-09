package com.leverx.RatingSystemRest.Business.Interfaces;

import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.AddCommentDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import java.util.List;

/**
 * Contract for Comment Service.
 */
@SuppressWarnings("checkstyle:Indentation")

public interface CommentService {

  /**
   * Adds a new comment.
   *
   * @param dto the data transfer object containing comment data
   */
  void add(AddCommentDto dto);

  /**
   * Deletes a comment.
   *
   * @param anonymousId the ID of the anonymous user
   * @param commentId   the ID of the comment

   */
  void delete(int anonymousId, int commentId);

  /**
   * Updates a comment.
   *
   * @param dto the data transfer object containing updated comment data
   */
  void update(CommentUpdateDto dto);

  /**
   * Gets approved reviews by seller ID.
   *
   * @param sellerId the ID of the seller
   * @return list of approved user reviews
   */
  List<UserReviewsDto> getApprovedReviewsBySellerId(int sellerId);

  /**
   * Gets not approved reviews by seller ID.
   *
   * @param sellerId the ID of the seller
   * @return list of not approved user reviews
   * @throws Exception if something goes wrong during processing
   */
  List<UserReviewsDto> getNotApprovedReviewsBySellerId(int sellerId) throws Exception;

  /**
   * Gets all not approved reviews.
   *
   * @return list of not approved user reviews
   */
  List<UserReviewsDto> getAllNotApprovedReviews();

  /**
   * Approves a user review.
   *
   * @param commentId the ID of the comment
   */
  void approveUserReview(int commentId);

  /**
   * Declines a user review.
   *
   * @param commentId the ID of the comment

   */
  void declineUserReview(int commentId);
}
