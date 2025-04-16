package com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used to represent a user's review of a seller.
 * This DTO encapsulates the relevant information for a review, including the review date, rating,
 * comment, seller's full name, and review ID. It is typically used for displaying a user's review
 * in response to requests.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewsDto {

  /**
   * The date when the review was published, formatted as "dd/MM/yyyy".
   * This field represents the publication date of the review, formatted in a human-readable date format.
   */
  private String publishDate;

  /**
   * The rating score given in the review (e.g., from 1 to 5).
   * This field stores the numerical rating given by the user, which reflects their experience or opinion.
   */
  private int review;

  /**
   * The content of the review or comment provided by the user.
   * This field holds the user's written feedback regarding the seller or product.
   */
  private String comment;

  /**
   * The full name of the seller being reviewed.
   * This field contains the name of the seller whose services or products were reviewed.
   */
  private String sellerFullName;

  /**
   * The unique identifier of the review.
   * This field represents the unique ID of the review, allowing it to be referenced in the system.
   */
  private int id;

  /**
   * Converts a `Comment` entity to a `UserReviewsDto`.
   * This method takes a `Comment` entity and converts it to a `UserReviewsDto` to be used in the response.
   * It formats the publish date and maps the relevant fields from the `Comment` entity to the DTO.
   *
   * @param review The `Comment` entity to be converted.
   * @return A `UserReviewsDto` representing the review.
   */
  public static UserReviewsDto toDto(Comment review) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return UserReviewsDto.builder()
        .publishDate(review.getCreatedAt().format(formatter))
        .review(review.getRating())
        .comment(review.getMessage())
        .sellerFullName(review.getUser().fullName())
        .id(review.getId())
        .build();
  }
}
