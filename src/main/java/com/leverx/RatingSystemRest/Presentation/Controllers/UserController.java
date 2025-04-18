package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages;
import com.leverx.RatingSystemRest.Business.Interfaces.UserService;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.IsAdminDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that handles user account operations.
 * Provides endpoints for authenticated users to manage their account settings
 * and check their roles in the system.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  /**
   * Changes the password for the currently authenticated user.
   * Validates that the old password is correct and the new password meets requirements.
   *
   * @param dto The DTO containing the old password, new password, and repeated new password
   * @param authentication The authentication object containing the user's credentials
   * @return ResponseEntity with the result message and appropriate HTTP status code:
   *         200 OK if password was changed successfully
   *         400 Bad Request if passwords don't match, new password is same as old, or old password is incorrect
   *         500 Internal Server Error for unexpected errors
   */
  @PutMapping("/password")
  public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDto dto, Authentication authentication) {
    var currentUserId = userService.retriaveLogedUserId(authentication);
    String result = userService.changePassword(currentUserId, dto);

    return switch (result) {
      case UserConstMessages.PASSWORD_CHANGED -> ResponseEntity.ok(result);
      case UserConstMessages.NEW_AND_REPEAT_PASSWORD_DOES_NOT_MATCH,
           UserConstMessages.PASSWORD_IS_SAME,
           UserConstMessages.INCORRECT_PASSWORD ->
          ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
      default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
    };
  }

  /**
   * Checks if the currently authenticated user has administrator privileges.
   *
   * @param authentication The authentication object containing the user's credentials
   * @return ResponseEntity containing a DTO with information about the user's admin status
   */
  @GetMapping("/verifyrole")
  public ResponseEntity<IsAdminDto> isAdmin(Authentication authentication) {
    IsAdminDto isAdminDto = userService.checkIfAdmin(authentication);

    return ResponseEntity.ok(isAdminDto);
  }
}