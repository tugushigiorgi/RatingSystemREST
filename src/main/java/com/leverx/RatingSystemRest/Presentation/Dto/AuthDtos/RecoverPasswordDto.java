package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used for password recovery.
 * This DTO contains the necessary information required for users to recover their password,
 * including a token for validation and the new password they want to set.
 */
@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class RecoverPasswordDto {

  /**
   * The token used to verify the user's identity during the password recovery process.
   * This token is typically sent to the user's email and is used to ensure that the
   * password recovery request is legitimate.
   */
  @NotBlank(message = "Token is required")
  public String token;

  /**
   * The new password that the user wants to set after recovering their password.
   * This password should be securely validated and stored after the recovery process.
   */
  @NotBlank(message = "password field is required")
  public String password;

  /**
   * The repeated new password to confirm that the user entered the correct password.
   * This field helps ensure that the user did not mistype their password.
   */
  @NotBlank(message = "repeat password field is required")
  public String newpassword;
}
