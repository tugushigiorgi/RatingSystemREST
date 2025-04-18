package com.leverx.RatingSystemRest.Business.impl;

import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.COMMENT_NOT_FOUND_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.PERMISSION_DENIED_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.SELLER_NOT_FOUND_MESSAGE;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of {@link CommentService} for handling comment operations.
 */
@Slf4j
@Service
@AllArgsConstructor
public class CommentServiceImp implements CommentService {

  private final CommentRepository commentRepository;
  private final UserRepository userRepository;

  /**
   * Adds a new comment to a seller if the anonymous user has not already submitted one.
   *
   * @param dto the data transfer object containing comment data
   */
  @Override
  public void add(AddCommentDto dto) {
    var currentSeller = userRepository.findById(dto.sellerId).orElseThrow(() -> {
      log.error("Seller not found: sellerId={}", dto.sellerId);
      return new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND_MESSAGE);
    });

    var hasMatchingAnonymousId = currentSeller.getComments().stream().anyMatch(comment -> comment.getAnonymousId().equals(dto.getAnonymousId()));

    if (hasMatchingAnonymousId) {
      log.warn("Duplicate anonymous comment detected for sellerId={}, anonymousId={}", dto.sellerId, dto.anonymousId);
      throw new ResponseStatusException(BAD_REQUEST);
    }

    var newComment = Comment.builder().approved(false).createdAt(LocalDateTime.now()).user(currentSeller).rating(dto.getReview()).message(dto.getComment()).anonymousId(dto.anonymousId).build();

    currentSeller.getComments().add(newComment);
    commentRepository.save(newComment);
    userRepository.save(currentSeller);
    log.info("Successfully added comment for sellerId={}, anonymousId={}", dto.sellerId, dto.anonymousId);

  }


  /**
   * Deletes a comment by its ID if the anonymous ID matches the comment's author.
   *
   * @param anonymousId the ID of the anonymous user
   * @param commentId   the ID of the comment to delete

   */
  @Override
  @Transactional
  public void delete(int anonymousId, int commentId) {
    log.info("Attempting to delete comment. CommentId={}, AnonymousId={}", commentId, anonymousId);

    var currentComment = commentRepository.findById(commentId).orElseThrow(() -> {
      log.error("Comment not found . CommentId={}", commentId);
      throw new ResponseStatusException(NOT_FOUND, COMMENT_NOT_FOUND_MESSAGE);
    });

    if (currentComment.getAnonymousId() != anonymousId) {
      log.warn("Permission denied for deleting comment. CommentId={}, ProvidedAnonymousId={}, ActualAnonymousId={}", commentId, anonymousId, currentComment.getAnonymousId());
      throw new ResponseStatusException(BAD_REQUEST, PERMISSION_DENIED_MESSAGE);
    }

    commentRepository.deleteById(commentId);
    log.info("Successfully deleted comment. CommentId={}, AnonymousId={}", commentId, anonymousId);

  }

  /**
   * Updates a comment's content or rating.
   *
   * @param dto the update request DTO
   * @return HTTP response indicating the result of the update
   */
  @Override
  public void update(CommentUpdateDto dto) {
    log.info("Attempting to update comment. CommentId={}, AnonymousId={}", dto.getCommentId(), dto.getAnonymousId());

    var currentComment = commentRepository.findById(dto.getCommentId()).orElseThrow(() -> {
      log.error("Comment not found. CommentId={}", dto.getCommentId());
      return new ResponseStatusException(NOT_FOUND, COMMENT_NOT_FOUND_MESSAGE);
    });

    if (currentComment.getAnonymousId() != dto.getAnonymousId()) {
      log.warn("Permission denied for updating comment. CommentId={}, ProvidedAnonymousId={}, ActualAnonymousId={}", dto.getCommentId(), dto.getAnonymousId(), currentComment.getAnonymousId());
      throw new ResponseStatusException(BAD_REQUEST);
    }

    boolean updated = false;

    if (dto.getComment() != null && !dto.getComment().equals(currentComment.getMessage())) {
      log.info("Updating comment message. CommentId={}, NewMessage={}", dto.getCommentId(), dto.getComment());
      currentComment.setMessage(dto.getComment());
      updated = true;
    }

    if (dto.getReview() != currentComment.getRating()) {
      log.info("Updating review rating. CommentId={}, OldRating={}, NewRating={}", dto.getCommentId(), currentComment.getRating(), dto.getReview());
      currentComment.setRating(dto.getReview());
      updated = true;
    }

    if (updated) {
      commentRepository.save(currentComment);
      log.info("Comment updated successfully. CommentId={}", dto.getCommentId());
    } else {
      log.info("No changes made to comment. CommentId={}", dto.getCommentId());
    }
  }


  /**
   * Retrieves all approved reviews for a given seller.
   *
   * @param sellerId the ID of the seller
   * @return a list of approved review DTOs
   */
  @Override
  public List<UserReviewsDto> getApprovedReviewsBySellerId(int sellerId) {
    log.info("Fetching approved reviews for seller. SellerId={}", sellerId);

    var seller = userRepository.findById(sellerId).orElseThrow(() -> {
      log.error("Seller Not found. SellerId={}", sellerId);
      return new ResponseStatusException(HttpStatus.NOT_FOUND, SELLER_NOT_FOUND_MESSAGE);
    });

    var reviews = commentRepository.sellersAllApprovedReviews(sellerId);

    if (CollectionUtils.isEmpty(reviews)) {
      log.info("No approved reviews found for seller. SellerId={}", sellerId);
      return emptyList();
    }

    log.info("Found {} approved reviews for seller. sellerId={}", reviews.size(), sellerId);
    return reviews.stream().map(UserReviewsDto::toDto).toList();
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
    log.info("Fetching not approved reviews for seller. SellerId={}", sellerId);

    var seller = userRepository.findById(sellerId).orElseThrow(() -> {
      log.error("Seller not found. SellerId={}", sellerId);
      return new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND_MESSAGE);
    });

    var reviews = commentRepository.sellersNotApprovedReviews(sellerId);

    if (CollectionUtils.isEmpty(reviews)) {
      log.info("No not approved reviews found for seller. SellerId={}", sellerId);
      return emptyList();
    }

    log.info("Found {} not approved reviews for seller. SellerId={}", reviews.size(), sellerId);
    return reviews.stream().map(UserReviewsDto::toDto).toList();
  }


  /**
   * Retrieves all unapproved reviews in the system.
   *
   * @return a list of unapproved review DTOs or empty list if not found.
   */
  @Override
  public List<UserReviewsDto> getAllNotApprovedReviews() {
    log.info("Fetching all not approved reviews.");

    var notApprovedReviews = commentRepository.allNotApprovedReviews();

    if (CollectionUtils.isEmpty(notApprovedReviews)) {
      log.info("No not approved reviews found.");
      return emptyList();
    }

    log.info("Found {} not approved reviews.", notApprovedReviews.size());
    return notApprovedReviews.stream()
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
    log.info("Approving review with commentId: {}", commentId);

    var comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, COMMENT_NOT_FOUND_MESSAGE));

    log.info("Found comment for approval: {}", comment);

    var seller = userRepository.findById(comment.getUser().getId())
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND_MESSAGE));

    log.info("Found seller with ID: {}", seller.getId());

    var approvedComments = seller.getComments().stream().filter(Comment::isApproved).toList();

    int count = approvedComments.size();
    int sum = (int) sumCurrentRating(approvedComments);

    double newAverage = (sum + comment.getRating()) / (double) (count + 1);
    log.info("Updating seller's total rating from {} to {}", seller.getTotalRating(), newAverage);

    seller.setTotalRating(newAverage);
    comment.setApproved(true);

    userRepository.save(seller);
    commentRepository.save(comment);

    log.info("Review approved and seller's rating updated successfully.");
  }


  /**
   * Declines and deletes a comment.
   *
   * @param commentId the ID of the comment to decline
   */
  @Override
  @Transactional
  public void declineUserReview(int commentId) {
    log.info("Attempting to decline review with commentId: {}", commentId);

    var comment = commentRepository.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, COMMENT_NOT_FOUND_MESSAGE));

    log.info("Found comment to decline: {}", comment);

    commentRepository.deleteById(commentId);
    log.info("Comment with commentId: {} has been successfully deleted.", commentId);
  }


  /**
   * Helper method to calculate the sum of all ratings from a list of comments.
   *
   * @param comments the list of comments
   * @return the total sum of ratings
   */
  public static double sumCurrentRating(List<Comment> comments) {
    log.debug("Calculating sum of ratings for {} comments", comments.size());

    double sum = comments.stream().mapToInt(Comment::getRating).sum();

    log.debug("Total sum of ratings: {}", sum);

    return sum;
  }

}
