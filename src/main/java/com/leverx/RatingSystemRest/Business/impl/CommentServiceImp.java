package com.leverx.RatingSystemRest.Business.impl;

import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.COMMENT_DELETED_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.COMMENT_NOT_FOUND_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.DELETED_SUCCESSFULLY_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.PERMISSION_DENIED_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.SELLER_NOT_FOUND_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.UPDATED_SUCCESSFULLY_MESSAGE;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.leverx.RatingSystemRest.Business.Interfaces.CommentService;
import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.CommentRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.AddCommentDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of {@link CommentService} for handling comment operations.
 */
@Service
@AllArgsConstructor
public class CommentServiceImp implements CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;

  /**
   * Adds a new comment to a seller if the anonymous user has not already submitted one.
   *
   * @param dto the data transfer object containing comment data
   * @return HTTP response containing success or failure message
   */
  @Override
  public void add(AddCommentDto dto) {
    var currentSeller = userRepository.findById(dto.sellerId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND_MESSAGE));

    var hasMatchingAnonymousId = currentSeller.getComments().stream()
        .anyMatch(comment -> comment.getAnonymousId().equals(dto.getAnonymousId()));

    if (hasMatchingAnonymousId) {
      throw new ResponseStatusException(BAD_REQUEST);
    }

    var newComment = Comment.builder()
        .approved(false)
        .createdAt(LocalDateTime.now())
        .user(currentSeller)
        .rating(dto.getReview())
        .message(dto.getComment())
        .anonymousId(dto.anonymousId)
        .build();

    currentSeller.getComments().add(newComment);
    commentRepository.save(newComment);
    userRepository.save(currentSeller);
  }


  /**
   * Deletes a comment by its ID if the anonymous ID matches the comment's author.
   *
   * @param anonymousId the ID of the anonymous user
   * @param commentId   the ID of the comment to delete
   * @return HTTP response indicating the result of the operation
   */
  @Override
  @Transactional
  public ResponseEntity<String> delete(int anonymousId, int commentId) {
    var currentComment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, COMMENT_NOT_FOUND_MESSAGE));

    if (currentComment.getAnonymousId() != anonymousId) {
      return new ResponseEntity<>(PERMISSION_DENIED_MESSAGE, BAD_REQUEST);
    }

    commentRepository.deleteById(commentId);
    return new ResponseEntity<>(DELETED_SUCCESSFULLY_MESSAGE, OK);
  }

  /**
   * Updates a comment's content or rating.
   *
   * @param dto the update request DTO
   * @return HTTP response indicating the result of the update
   */
  @Override
  public void update(CommentUpdateDto dto) {
    var currentComment = commentRepository.findById(dto.getCommentId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, COMMENT_NOT_FOUND_MESSAGE));

    if (currentComment.getAnonymousId() != dto.getAnonymousId()) {
      throw new ResponseStatusException(BAD_REQUEST);
    }

    if (dto.getComment() != null && !dto.getComment().equals(currentComment.getMessage())) {
      currentComment.setMessage(dto.getComment());
    }

    if (dto.getReview() != currentComment.getRating()) {
      currentComment.setRating(dto.getReview());
    }

    commentRepository.save(currentComment);
  }


  /**
   * Retrieves all approved reviews for a given seller.
   *
   * @param sellerId the ID of the seller
   * @return a list of approved review DTOs or 204 if none are found
   */
  @Override
  public ResponseEntity<List<UserReviewsDto>> getApprovedReviewsBySellerId(int sellerId) {
    var seller = userRepository.findById(sellerId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND_MESSAGE));

    var reviews = commentRepository.sellersAllApprovedReviews(sellerId);
    if (reviews.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    var reviewDtos = reviews.stream().map(UserReviewsDto::toDto).toList();
    return ResponseEntity.ok(reviewDtos);
  }

  /**
   * Retrieves all unapproved reviews for a given seller.
   *
   * @param sellerId the ID of the seller
   * @return a list of unapproved review DTOs
   * @throws Exception if the seller is not found
   */
  @Override
  public List<UserReviewsDto> getNotApprovedReviewsBySellerId(int sellerId) throws Exception {
    var seller = userRepository.findById(sellerId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND_MESSAGE));

    var reviews = commentRepository.sellersNotApprovedReviews(sellerId);
    return reviews.stream().map(UserReviewsDto::toDto).toList();
  }

  /**
   * Retrieves all unapproved reviews in the system.
   *
   * @return a list of unapproved review DTOs or empty list if not found.
   */
  @Override
  public List<UserReviewsDto> getAllNotApprovedReviews() {
    var notApprovedReviews = commentRepository.allNotApprovedReviews();
    if (CollectionUtils.isEmpty(notApprovedReviews)) {
      return emptyList();
    }
    return notApprovedReviews
        .stream()
        .map(UserReviewsDto::toDto)
        .toList();
  }


  /**
   * Approves a user review and recalculates the seller’s average rating.
   *
   * @param commentId the ID of the comment to approve
   * @return HTTP response indicating the result of the operation
   */
  @Override
  public void approveUserReview(int commentId) {
    var comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, COMMENT_NOT_FOUND_MESSAGE));

    var seller = userRepository.findById(comment.getUser().getId())
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND_MESSAGE));

    var approvedComments = seller.getComments().stream()
        .filter(Comment::isApproved)
        .toList();

    int count = approvedComments.size();
    int sum = (int) sumCurrentRating(approvedComments);

    double newAverage = (sum + comment.getRating()) / (double) (count + 1);
    seller.setTotalRating(newAverage);

    comment.setApproved(true);

    userRepository.save(seller);
    commentRepository.save(comment);
  }


  /**
   * Declines and deletes a comment.
   *
   * @param commentId the ID of the comment to decline
   * @return HTTP response indicating successful deletion
   */
  @Override
  @Transactional
  public ResponseEntity<String> declineUserReview(int commentId) {
    var comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, COMMENT_NOT_FOUND_MESSAGE));
    commentRepository.deleteById(commentId);
    return new ResponseEntity<>(COMMENT_DELETED_MESSAGE, OK);
  }

  /**
   * Helper method to calculate the sum of all ratings from a list of comments.
   *
   * @param comments the list of comments
   * @return the total sum of ratings
   */
  public static double sumCurrentRating(List<Comment> comments) {
    return comments.stream().mapToInt(Comment::getRating).sum();
  }
}
