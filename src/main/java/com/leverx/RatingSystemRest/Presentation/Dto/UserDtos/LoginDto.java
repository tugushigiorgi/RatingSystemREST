package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for user login credentials.
 * This DTO is used to transfer login information, including email and password, between the client and the server.
 * It ensures that the provided email is valid and that the password meets the required minimum length.
 */
@Builder
@Data
@Getter
@Setter
public class LoginDto {

  /**
   * The email address of the user attempting to log in.
   * This field is required and must follow the correct email format.
   * Validation:
   * - Must not be blank.
   * - Must be a valid email format.
   */
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  private String email;

  /**
   * The password of the user attempting to log in.
   * This field is required and must have a minimum length of 6 characters.
   * Validation:
   * - Must not be blank.
   * - Must be at least 6 characters long.
   */
  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "password must be at least 6 characters long")
  private String password;
}
