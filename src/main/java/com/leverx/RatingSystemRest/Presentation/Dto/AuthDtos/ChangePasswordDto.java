package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object (DTO) used for changing a user's password.
 * This class represents the input structure for a password change request, containing
 * the old password, the new password, and a confirmation of the new password (repeat password).
 * The fields are validated to ensure that passwords are non-blank and meet the minimum length requirement.
 */
@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {

  /**
   * The user's old password.
   * This field is required and must not be blank. The password must be at least 6 characters long.
   */
  @NotBlank(message = "Old password is required")
  @Size(min = 6, message = "old password field must be at least 6 characters long")
  public String oldPassword;

  /**
   * The user's new password.
   * This field is required and must not be blank. The password must be at least 6 characters long.
   */
  @NotBlank(message = "New password is required")
  @Size(min = 6, message = "new  password field must be at least 6 characters long")
  public String newPassword;

  /**
   * The confirmation of the user's new password.
   * This field is required and must not be blank. The password must be at least 6 characters long.
   */
  @NotBlank(message = "Repeat password is required")
  @Size(min = 6, message = "repeat password field must be at least 6 characters long")
  public String repeatPassword;

}
