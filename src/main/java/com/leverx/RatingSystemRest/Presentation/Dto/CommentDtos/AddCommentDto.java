package com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used to add a new comment or review.
 * This DTO captures the necessary information for submitting a review for a seller,
 * including the review score, the comment itself, and associated identifiers.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentDto {

  /**
   * The review score given by the user, which must be between 1 and 5.
   * This field represents the user's rating of the seller and is validated to be within
   * the range of 1 to 5.
   */
  @Min(value = 1, message = "review must be at least 1")
  @Max(value = 5, message = "review cannot be greater than 5")
  @NotNull(message = " review field is required")
  public int review;

  /**
   * The identifier of the seller being reviewed.
   * This field links the review to a specific seller in the system.
   */
  @NotNull(message = "sellerId field is required")
  public int sellerId;

  /**
   * The comment provided by the user as part of their review.
   * This text provides additional feedback about the seller and is mandatory for submission.
   */
  @NotBlank(message = "comment field is required")
  public String comment;

  /**
   * The identifier of the anonymous user submitting the review.
   * This field represents the user (often an anonymous user) providing feedback, linking
   * the comment to their profile or session.
   */
  @NotNull(message = "anonymousId field is required")
  public int anonymousId;

}
