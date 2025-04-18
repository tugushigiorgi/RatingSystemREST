package com.leverx.RatingSystemRest.Business.Interfaces;


import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.IsAdminDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.JwtDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.AdminNotApprovedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.DetailedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.LoginDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.RegisterUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.SellerProfileDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for managing user-related operations such as registration,
 * authentication, profile retrieval, and administration.
 */
public interface UserService {

  /**
   * Retrieves a list of seller registration requests awaiting approval.
   *
   * @return a response containing the list of pending seller requests
   */
  List<AdminNotApprovedUserDto> getSellersRegistrationRequests();

  /**
   * Accepts a pending seller registration request.
   *
   * @param sellerId the ID of the seller to approve
   */
  void acceptSellerRegistrationRequest(int sellerId);

  /**
   * Retrieves a list of all detailed registered users.
   *
   * @return a response containing the list of detailed users
   */
  List<DetailedUserDto> detailedRegisteredUsers();

  /**
   * Retrieves detailed user information filtered by username.
   *
   * @param username the username to filter users by
   * @return a response containing the list of matched users
   */
  List<DetailedUserDto> getDetailedRegisteredUsersByUsername(String username);

  /**
   * Deletes a user by their ID.
   *
   * @param userId the ID of the user to delete
   */
  void deleteById(int userId);

  /**
   * Retrieves user info by user ID.
   *
   * @param userId the ID of the user
   * @return a response containing the user's information
   */
  ResponseEntity<UserInfoDto> getUserInfoById(int userId);

  /**
   * Changes the password for a user.
   *
   * @param currentuserId the ID of the currently authenticated user
   * @param dto           the password change request data
   * @return a response indicating the result of the operation
   */
  ResponseEntity<String> changePassword(int currentuserId, ChangePasswordDto dto);

  /**
   * Retrieves a list of top-rated sellers.
   *
   * @return a response containing the list of top-rated sellers
   */
  List<UserInfoDto> getTopRatedSellers();

  /**
   * Retrieves a seller's profile by user ID.
   *
   * @param userId the ID of the seller
   * @return  SellerProfileDto
   */
  SellerProfileDto getSellerProfileById(int userId);

  /**
   * Registers a new user with profile photo.
   *
   * @param dto   the user registration data
   * @param photo the profile photo to be uploaded
   */
  void registerUser(RegisterUserDto dto, MultipartFile photo);

  /**
   * login user.
   *
   * @param loginDto the user login data
   * @return a response containing the seller's profile
   */

  JwtDto login(LoginDto loginDto);

  /**
   * Sends a registration confirmation email.
   *
   * @param user the user to send the email to
   */
  void sendRegistrationEmail(User user);

  /**
   * Retrieves the ID of the currently logged-in user.
   *
   * @param authentication the authentication object from Spring Security
   * @return the user ID of the authenticated user
   */
  int retriaveLogedUserId(Authentication authentication);

  /**
   * Checks whether the authenticated user has admin privileges.
   *
   * @param authentication the authentication object from Spring Security
   * @return a response indicating if the user is an admin
   */
  ResponseEntity<IsAdminDto> checkifAdmin(Authentication authentication);

  /**
   * Send Password recovery code Email.
   *
   * @param email recipient
   */
  void sendRecoverCode(String email);

  /**
   * Update Password.
   *
   * @param dto new and old password dto.
   */
  void updatePassword(RecoverPasswordDto dto);

  /**
   * activate account with token sent in mail.
   */
  void activateAccount(String token);


  /**
   * cleans securityContextHolder.
   */
  void logout(HttpServletRequest request);
}
