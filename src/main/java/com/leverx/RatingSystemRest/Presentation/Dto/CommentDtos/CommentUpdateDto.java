package com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used to update an existing comment or review.
 * This DTO captures the necessary information for modifying an existing review,
 * including the comment ID, the updated review score, comment content, and associated identifiers.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDto {

  /**
   * The unique identifier of the comment being updated.
   * This field specifies which comment should be updated in the system.
   */
  @NotNull(message = "comment id required")
  public int commentId;

  /**
   * The updated review score given by the user, representing the rating for the seller.
   * This field specifies the new score for the review. It is required and cannot be null.
   */
  @NotNull(message = "review field required")
  public int review;

  /**
   * The identifier of the seller being reviewed.
   * This field links the updated review to a specific seller in the system.
   */
  @NotBlank(message = "sellerId field is required")
  public int sellerId;

  /**
   * The updated comment provided by the user as part of the review.
   * This text represents the new feedback for the seller and must not be blank.
   */
  @NotBlank(message = "comment field is required")
  public String comment;

  /**
   * The identifier of the anonymous user submitting the updated review.
   * This field represents the user (often an anonymous user) updating the review, linking
   * the comment to their profile or session.
   */
  @NotNull(message = "anonymousId field is required")
  public int anonymousId;
}
