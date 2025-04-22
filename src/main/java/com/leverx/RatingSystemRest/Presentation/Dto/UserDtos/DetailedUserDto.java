package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) that holds detailed user information.
 * This DTO is used to provide detailed information about a user, including their ID, profile picture, full name, email,
 * account creation date, total rating, and a list of approved user reviews. It is typically used for showing user
 * details in a detailed view, such as the user's profile page.
 */
@Builder
@Data
@Getter
@Setter
public class DetailedUserDto {

  /**
   * The unique identifier for the user.
   * This field represents the user's unique ID in the system.
   */
  private int id;

  /**
   * The URL to the user's profile picture.
   * This field holds the location of the user's profile picture.
   */
  private String pictureUrl;

  /**
   * The full name of the user.
   * This field holds the full name of the user.
   */
  private String fullName;

  /**
   * The email address of the user.
   * This field holds the email address of the user.
   */
  private String email;

  /**
   * The date when the user account was created.
   * This field represents the account creation date of the user in "dd/MM/yyyy" format.
   */
  private String date;

  /**
   * The total rating of the user.
   * This field holds the total rating of the user, formatted to two decimal places.
   */
  private String totalRating;

  /**
   * A list of approved reviews associated with the user.
   * This field holds a list of reviews given by other users, which have been approved.
   * Each review is represented by a {@link UserReviewsDto} object.
   */
  private List<UserReviewsDto> reviews;

  /**
   * Converts a {@link User} entity to a {@link DetailedUserDto}.
   * This method creates a new DTO from the given `User` entity, extracting relevant user information, including
   * their approved reviews and total rating, and formatting the creation date.
   *
   * @param user The `User` entity from which to create the DTO.
   * @return An instance of {@link DetailedUserDto} containing detailed information about the user.
   */
  public static DetailedUserDto toDetailedUserDto(User user) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    var reviewsList = user.getComments().stream()
        .filter(Comment::isApproved)
        .map(UserReviewsDto::toDto)
        .toList();
    return DetailedUserDto.builder()
        .id(user.getId())
        .pictureUrl(user.getPhoto().getUrl())
        .fullName(user.fullName())
        .email(user.getEmail())
        .date(user.getCreatedAt().format(formatter))
        .totalRating(String.format("%.2f", user.getTotalRating()))
        .reviews(reviewsList)
        .build();
  }
}
