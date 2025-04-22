package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for checking if a user has admin privileges.
 * This DTO contains a boolean field indicating whether the user is an administrator.
 * It is typically used in authorization processes to determine access control based on the user's role.
 */
@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class IsAdminDto {

  /**
   * A boolean indicating whether the user is an admin.
   * This field is set to true if the user has admin privileges, and false otherwise.
   */
  public boolean isAdmin;
}
