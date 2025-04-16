package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for user registration.
 * Contains the necessary information required when a new user registers
 * in the system, including name, surname, email, and password.
 * Includes validation annotations to ensure the integrity of the input.
 */
@Builder
@Getter
@Setter
public class RegisterUserDto {

  /**
   * User's first name.
   * Must not be blank.
   */
  @NotBlank(message = "Name is required")
  public String name;

  /**
   * User's last name.
   * Must not be blank.
   */
  @NotBlank(message = "Surname is required")
  public String surname;

  /**
   * User's email address.
   * Must not be blank and must follow valid email format.
   */
  @NotBlank(message = "Email is required")
  @Email(message = "invalid email format")
  public String email;

  /**
   * User's password.
   * Must not be null and must be at least 6 characters long.
   */
  @NotNull
  @Size(min = 6, message = "password must be at least 6 characters long")
  public String password;
}
