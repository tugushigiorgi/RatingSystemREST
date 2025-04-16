package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object that contains basic user profile information.
 * Used to present user identity, registration date, and rating details.
 */
@Builder
@Getter
@Setter
public class UserInfoDto {

  /**
   * Unique identifier of the user.
   */
  public int id;

  /**
   * Full name of the user.
   */
  public String fullName;

  /**
   * Email address of the user.
   */
  public String email;

  /**
   * Date when the user registered, formatted as dd/MM/yyyy.
   */
  public String registretionDate;

  /**
   * URL to the user's profile picture.
   */
  public String pictureUrl;

  /**
   * Average user rating, formatted to two decimal places.
   */
  public String rating;

  /**
   * Converts a User entity to a UserInfoDto.
   *
   * @param user the User entity to convert
   * @return a populated UserInfoDto instance
   */
  public static UserInfoDto toDto(User user) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    return UserInfoDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .pictureUrl(user.getPhoto().getUrl())
        .rating(String.format("%.2f", user.getTotalRating()))
        .registretionDate(user.getCreatedAt().format(formatter))
        .fullName(user.fullName())
        .build();
  }
}
