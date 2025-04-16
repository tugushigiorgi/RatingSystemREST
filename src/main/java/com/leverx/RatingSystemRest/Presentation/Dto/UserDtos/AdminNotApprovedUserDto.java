package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing an admin 's view of a user who has not yet been approved.
 * This DTO carries the essential user information, including the user's ID, full name, email, profile picture URL,
 * and the date the account was created. It is intended for use in the admin panel to manage unapproved users.
 */
@Data
@Builder
@Getter
@Setter
public class AdminNotApprovedUserDto {

  /**
   * The unique identifier of the user.
   * This field holds the user's ID.
   */
  public int id;

  /**
   * The URL to the user's profile picture.
   * This field holds the location of the user's profile picture.
   */
  public String pictureUrl;

  /**
   * The full name of the user.
   * This field holds the user's full name.
   */
  public String fullName;

  /**
   * The email address of the user.
   * This field holds the user's email address.
   */
  public String email;

  /**
   * The date when the user account was created.
   * This field holds the formatted creation date of the user account.
   */
  public String date;

  /**
   * Converts a {@link User} entity to an {@link AdminNotApprovedUserDto}.
   * This method creates a new DTO from the given User entity, extracting relevant information and formatting the date.
   *
   * @param user The User entity from which to create the DTO.
   * @return An instance of {@link AdminNotApprovedUserDto} with data from the provided User.
   */
  public static AdminNotApprovedUserDto toDto(User user) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    return AdminNotApprovedUserDto.builder()
        .id(user.getId())
        .date(user.getCreatedAt().format(formatter))
        .email(user.getEmail())
        .fullName(user.fullName())
        .pictureUrl(user.getPhoto().getUrl())
        .build();
  }
}
