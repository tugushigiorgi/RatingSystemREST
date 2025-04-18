package com.leverx.RatingSystemRest.Presentation.Controllers;

import static com.leverx.RatingSystemRest.Business.ConstMessages.AuthConstMessages.LOGGED_OUT_SUCCESSFULLY;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.EMAIL_SENT_SUCCESSFULLY;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.PASSWORD_UPDATED_SUCCESSFULLY;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.SUCCESSFULLY_ENABLED;

import com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.JwtDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.LoginDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.RegisterUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller that handles user authentication operations.
 * Provides endpoints for user registration, login, password recovery, email verification, and logout.
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

  private final UserServiceImpl userServiceImpl;

  /**
   * Registers a new user in the system with their details and profile photo.
   *
   * @param dto The DTO containing user registration information
   * @param photo The user's profile photo as a MultipartFile
   * @return ResponseEntity with a success message upon registration
   */
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(
      @ModelAttribute RegisterUserDto dto,
      @RequestParam("photo") MultipartFile photo) {
    userServiceImpl.registerUser(dto, photo);
    return ResponseEntity.ok(UserConstMessages.USER_REGISTERED_SUCCESSFULLY);
  }

  /**
   * Authenticates a user and provides a JWT token for authorization.
   *
   * @param loginDto The DTO containing user login credentials
   * @return ResponseEntity containing a JWT token upon successful authentication
   */
  @PostMapping("/login")
  public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginDto loginDto) {
    JwtDto jwtDto = userServiceImpl.login(loginDto);
    return ResponseEntity.ok(jwtDto);
  }

  /**
   * Sends a password recovery code to the specified email address.
   *
   * @param email The email address to which the recovery code will be sent
   * @return ResponseEntity with a success message upon sending the email
   */
  @PostMapping("/recovercode/{email}")
  public ResponseEntity<String> passwordRecoverCodeSend(@PathVariable String email) {
    userServiceImpl.sendRecoverCode(email);
    return ResponseEntity.ok(EMAIL_SENT_SUCCESSFULLY);
  }

  /**
   * Updates a user's password using the recovery code and new password.
   *
   * @param dto The DTO containing the recovery code and new password information
   * @return ResponseEntity with a success message upon password update
   */
  @PostMapping("/updatepassword")
  public ResponseEntity<String> changePassword(@Valid @RequestBody RecoverPasswordDto dto) {
    userServiceImpl.updatePassword(dto);
    return ResponseEntity.ok(PASSWORD_UPDATED_SUCCESSFULLY);
  }

  /**
   * Verifies a user's email address using the verification code sent to them.
   *
   * @param code The verification code sent to the user's email
   * @return ResponseEntity with a success message upon successful verification
   */
  @PostMapping("/verify/{code}")
  public ResponseEntity<String> verifyEmailCode(@PathVariable String code) {
    userServiceImpl.activateAccount(code);
    return ResponseEntity.ok(SUCCESSFULLY_ENABLED);
  }

  /**
   * Logs out a user by invalidating their session or token.
   *
   * @param request The HttpServletRequest containing the user's session information
   * @return ResponseEntity with a success message upon logout
   */
  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    userServiceImpl.logout(request);
    return ResponseEntity.ok(LOGGED_OUT_SUCCESSFULLY);
  }
}