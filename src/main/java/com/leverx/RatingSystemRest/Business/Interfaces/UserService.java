package com.leverx.RatingSystemRest.Business.Interfaces;


import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.IsAdminDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.*;
import com.leverx.RatingSystemRest.Presentation.ServiceResponse;
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
   * @return the list of pending seller requests
   */
  List<AdminNotApprovedUserDto> getSellersRegistrationRequests();

  /**
   * Accepts a pending seller registration request.
   *
   * @param sellerId the ID of the seller to approve
   * @return a response containing the result of the operation
   */
  boolean acceptSellerRegistrationRequest(int sellerId);

  /**
   * Retrieves a list of all detailed registered users.
   *
   * @return list of detailed users
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
  ResponseEntity<List<UserInfoDto>> getTopRatedSellers();

  /**
   * Retrieves a seller's profile by user ID.
   *
   * @param userId the ID of the seller
   * @return a response containing the seller's profile
   */
  ResponseEntity<SellerProfileDto> getSellerProfileById(int userId);

  /**
   * Registers a new user with profile photo.
   *
   * @param dto   the user registration data
   * @param photo the profile photo to be uploaded
   * @return a ServiceResponse
   */
  ServiceResponse registerUser(RegisterUserDto dto, MultipartFile photo);

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
}
