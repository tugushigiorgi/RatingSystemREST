package com.leverx.RatingSystemRest.Presentation.Controllers;

import static com.leverx.RatingSystemRest.Presentation.ConstMessages.AuthConstMessages.LOGGED_OUT;
import static com.leverx.RatingSystemRest.Presentation.ConstMessages.AuthConstMessages.LOG_OUT_FAILED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

import com.leverx.RatingSystemRest.Business.impl.UserServiceImp;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.JwtDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.LoginDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.RegisterUserDto;
import com.leverx.RatingSystemRest.Presentation.ServiceResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling authentication and user account actions such as registration,
 * login, password recovery, and email verification.
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

  private final UserServiceImp userServiceImpl;

  /**
   * Registers a new user by saving their information and profile picture.
   *
   * @param dto   The DTO containing user registration data.
   * @param photo The profile picture of the user.
   * @return ResponseEntity containing the result message and HTTP status.
   */
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@ModelAttribute RegisterUserDto dto, @RequestParam("photo") MultipartFile photo) {
    ServiceResponse serviceResponse = userServiceImpl.registerUser(dto, photo);
    return new ResponseEntity<>(serviceResponse.getMessage(), serviceResponse.getStatus());
  }

  /**
   * Authenticates a user and generates a JWT token if the credentials are correct.
   *
   * @param loginDto The DTO containing login credentials (email and password).
   * @return ResponseEntity containing the JWT token if successful, or an error message and status.
   */
  @PostMapping("/login")
  public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginDto loginDto) {
    ServiceResponse serviceResponse = userServiceImpl.login(loginDto);

    if (serviceResponse.isSuccess()) {
      return ResponseEntity.ok(serviceResponse.getJwt());
    }
    return ResponseEntity.status(serviceResponse.getStatus()).body(null);
  }

  /**
   * Sends a password recovery code to the specified email.
   *
   * @param email The email address of the user who is requesting a password recovery.
   * @return ResponseEntity containing the result message and HTTP status.
   */
  @PostMapping("/recovercode/{email}")
  public ResponseEntity<String> passwordRecoverCodeSend(@PathVariable String email) {
    ServiceResponse serviceResponse = userServiceImpl.sendRecoverCode(email);
    return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getMessage());
  }

  /**
   * Updates the password for a user based on the provided recovery data.
   *
   * @param dto The DTO containing the password recovery token and new password.
   * @return ResponseEntity containing the result message and HTTP status.
   */
  @PostMapping("/updatepassword")
  public ResponseEntity<String> changePassword(@Valid @RequestBody RecoverPasswordDto dto) {
    ServiceResponse serviceResponse = userServiceImpl.updatePassword(dto);
    return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getMessage());
  }

  /**
   * Verifies the email account using a provided verification code.
   *
   * @param code The email verification code.
   * @return ResponseEntity containing the result message and HTTP status.
   */
  @PostMapping("/verify/{code}")
  public ResponseEntity<String> verifyEmailCode(@PathVariable String code) {
    ServiceResponse serviceResponse = userServiceImpl.activateAccount(code);
    return ResponseEntity.status(serviceResponse.getStatus()).body(serviceResponse.getMessage());
  }

  /**
   * Logs out the current user by invalidating the session and clearing the security context.
   *
   * @param request The HttpServletRequest to retrieve the session.
   * @return ResponseEntity containing a success or failure message based on the outcome.
   */
  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request) {
    try {
      request.getSession().invalidate();
      SecurityContextHolder.clearContext();
      return new ResponseEntity<>(LOGGED_OUT, OK);
    } catch (Exception e) {
      return new ResponseEntity<>(LOG_OUT_FAILED, INTERNAL_SERVER_ERROR);
    }
  }
}
