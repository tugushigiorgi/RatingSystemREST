package com.leverx.RatingSystemRest.Business.impl;

import static com.leverx.RatingSystemRest.Business.ConstMessages.AuthConstMessages.LOGGED_OUT_FAILED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.PICTURE_CANNOT_BE_SAVED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.ACCOUNT_IS_ALREADY_VERIFIED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.AUTHENTICATION_FAILED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.EMAIL_NOT_VERIFIED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.INVALID_TOKEN;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.PASSWORD_DOES_NOT_MATCH;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.SELLER_NOT_FOUND;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.TOKEN_EXPIRED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.TOKEN_EXPIRED_NEW_TOKEN_SEND;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.TOKEN_NOT_FOUND;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.USER_NOT_APPROVED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.USER_NOT_FOUND;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages;
import com.leverx.RatingSystemRest.Business.Interfaces.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Entities.PasswordRecoveryToken;
import com.leverx.RatingSystemRest.Infrastructure.Entities.Token;
import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Infrastructure.Entities.UserPhoto;
import com.leverx.RatingSystemRest.Infrastructure.Entities.UserRoleEnum;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.PasswordRecTokenRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.TokenRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserPhotoRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Infrastructure.Security.JwtFactory;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.IsAdminDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.JwtDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.AdminNotApprovedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.DetailedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.LoginDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.RegisterUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.SellerProfileDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service which implement UserService interface.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  @Value("${file.upload-dir}")
  private String uploadDir;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserPhotoRepository userPhotoRepository;
  private final EmailServiceImp emailService;
  private final TokenRepository tokenRepository;
  private final PasswordRecTokenRepository pwdRecoveryTokenRepository;
  private final JwtFactory jwtFactory;
  private final AuthenticationManager authenticationManager;

  /**
   * Retrieves all pending seller registration requests.
   *
   * @return a list of users pending approval as {@link AdminNotApprovedUserDto}, or empty  if none found.
   */
  public List<AdminNotApprovedUserDto> getSellersRegistrationRequests() {
    log.info("Fetching list of sellers who have not been approved yet.");

    var getlist = userRepository.notApprovedSellersList();

    if (CollectionUtils.isEmpty(getlist)) {
      log.warn("No registration requests found for sellers.");

      return emptyList();
    }
    log.info("Successfully retrieved {} seller registration requests.", getlist.size());

    return getlist.stream()
        .map(AdminNotApprovedUserDto::toDto)
        .toList();
  }


  /**
   * Approves a seller registration by ID.
   *
   * @param sellerId the ID of the seller to approve
   */
  public void acceptSellerRegistrationRequest(int sellerId) {
    log.info("Attempting to approve seller with ID: {}", sellerId);

    var currentSeller = userRepository.findById(sellerId)
        .orElseThrow(() -> {
          log.error("Seller with ID {} not found.", sellerId);
          return new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND);
        });

    if (!currentSeller.isHasVerifiedEmail()) {
      log.warn("Seller with ID {} has not verified their email.", sellerId);
      throw new ResponseStatusException(BAD_REQUEST, EMAIL_NOT_VERIFIED);
    }

    currentSeller.setApprovedByAdmin(true);
    userRepository.save(currentSeller);
    log.info("Seller with ID {} has been approved by the admin.", sellerId);
  }


  /**
   * Retrieves a detailed list of approved seller users.
   *
   * @return list of {@link DetailedUserDto} or empty
   */
  @Override
  public List<DetailedUserDto> detailedRegisteredUsers() {

    log.info("Fetching list of approved sellers...");

    var users = userRepository.approvedSellersList();

    if (CollectionUtils.isEmpty(users)) {
      log.info("No approved sellers found.");
      return emptyList();
    }

    log.info("Found {} approved seller(s).", users.size());

    return users.stream()
        .map(DetailedUserDto::toDetailedUserDto)
        .toList();
  }


  /**
   * Retrieves detailed seller information filtered by username.
   *
   * @param username the seller's username
   * @return a list of matching sellers or NO_CONTENT
   */
  @Override
  public List<DetailedUserDto> getDetailedRegisteredUsersByUsername(String username) {
    log.info("Fetching approved sellers with username: {}", username);

    var users = userRepository.getRegisteredSellerByUsername(username);

    if (CollectionUtils.isEmpty(users)) {
      log.info("No approved sellers found for username: {}", username);
      return emptyList();
    }

    log.info("Found {} seller(s) with username: {}", users.size(), username);

    return users.stream()
        .map(DetailedUserDto::toDetailedUserDto)
        .toList();
  }


  /**
   * Deletes a user by ID along with their profile picture folder.
   *
   * @param userId the ID of the user to delete
   * @return a confirmation message or error if user not found
   */
  @Override
  @Transactional
  public void deleteById(int userId) {
    log.info("Attempting to delete user with ID: {}", userId);

    var user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("User with ID {} not found", userId);
          return new ResponseStatusException(BAD_REQUEST, USER_NOT_FOUND);
        });

    log.info("User found: {}. Proceeding with deletion.", user.getUsername());

    String userFolderPath = uploadDir + File.separator + userId;
    deleteUserFolderByUrl(userFolderPath);
    log.info("Deleted user's folder from path: {}", userFolderPath);

    userRepository.deleteById(userId);
    log.info("Deleted user with ID: {}", userId);
  }



  /**
   * Deletes a user's folder and its contents from the filesystem.
   *
   * @param folderUrl the path to the folder
   * @return true if deleted successfully, false otherwise
   */
  private boolean deleteUserFolderByUrl(String folderUrl) {
    try {
      var folderPath = Paths.get(folderUrl);

      if (!Files.exists(folderPath)) {
        log.warn("Folder does not exist: {}", folderUrl);
        return false;
      }

      deleteRecursively(folderPath);
      log.info("Folder and its contents deleted: {}", folderUrl);
      return true;

    } catch (Exception e) {
      log.error("Failed to delete folder: {}. Error: {}", folderUrl, e.getMessage(), e);
      return false;
    }
  }


  /**
   * Recursively deletes all files and folders in the specified path.
   *
   * @param path the path to delete
   * @throws IOException if any file or directory cannot be deleted
   */
  private static void deleteRecursively(Path path) throws IOException {
    try (Stream<Path> files = Files.walk(path)) {
      files
          .sorted(Comparator.reverseOrder())
          .forEach(p -> {
            try {
              Files.delete(p);
              log.debug("Deleted: {}", p);
            } catch (IOException e) {
              log.error("Failed to delete: {}", p, e);
            }
          });
    }
  }


  /**
   * Fetches public information for a user by ID.
   *
   * @param userId the ID of the user
   * @return {@link UserInfoDto} or throws exception
   */
  @Override
  public UserInfoDto getUserInfoById(int userId) {
    log.info("Attempting to retrieve user info for userId: {}", userId);

    var user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("User  with ID {} not found", userId);
          return new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND);
        });

    log.info("Successfully retrieved user info for userId: {}", userId);
    return UserInfoDto.toDto(user);
  }



  /**
   * Allows a user to change their password.
   *
   * @param currentUserId the ID of the user requesting the change
   * @param dto           the DTO containing old and new password
   * @return string containint information
   */
  @Override
  public String changePassword(int currentUserId, ChangePasswordDto dto) {
    log.info("User {} is attempting to change their password", currentUserId);

    var user = userRepository.findById(currentUserId)
        .orElseThrow(() -> {
          log.warn("User with ID {} not found while attempting password change", currentUserId);
          return new ResponseStatusException(BAD_REQUEST, UserConstMessages.USER_NOT_FOUND);
        });

    if (!dto.newPassword.equals(dto.repeatPassword)) {
      log.warn("Password mismatch for user {}: new and repeat password do not match", currentUserId);
      return UserConstMessages.NEW_AND_REPEAT_PASSWORD_DOES_NOT_MATCH;
    }

    if (passwordEncoder.encode(dto.oldPassword).equals(user.getPassword())) {
      log.warn("User {} tried to change password to the same one", currentUserId);
      return UserConstMessages.PASSWORD_IS_SAME;
    }

    if (!passwordEncoder.matches(dto.oldPassword, user.getPassword())) {
      log.warn("User {} provided incorrect old password", currentUserId);
      return UserConstMessages.INCORRECT_PASSWORD;
    }

    user.setPassword(passwordEncoder.encode(dto.newPassword));
    userRepository.save(user);
    log.info("Password changed successfully for user {}", currentUserId);
    return UserConstMessages.PASSWORD_CHANGED;
  }


  /**
   * Fetches a list of top-rated seller users.
   *
   * @return list of {@link UserInfoDto} or empty
   */
  @Override
  public List<UserInfoDto> getTopRatedSellers() {
    log.info("Fetching top 5 rated sellers.");
    var sellers = userRepository.findTop5RatedSellers();

    if (CollectionUtils.isEmpty(sellers)) {
      log.warn("No top rated sellers found.");
      return emptyList();
    }

    log.info("Found {} top rated sellers.", sellers.size());
    return sellers.stream()
        .map(UserInfoDto::toDto)
        .toList();
  }


  /**
   * Retrieves a seller's full profile by user ID, including game objects.
   *
   * @param userId the seller's user ID
   * @return {@link SellerProfileDto} or NO_CONTENT if no games
   */
  @Override
  public SellerProfileDto getSellerProfileById(int userId) {
    log.info("Fetching seller profile for userId: {}", userId);

    var currentSeller = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("Seller with userId {} not found.", userId);
          return new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND);
        });

    var info = UserInfoDto.toDto(currentSeller);
    var gameList = currentSeller.getGameObjects();

    if (CollectionUtils.isEmpty(gameList)) {
      log.info("No game objects found for seller with userId: {}", userId);
      return SellerProfileDto.builder()
          .userInfo(info)
          .gameObjects(emptyList())
          .build();
    }

    log.info("Found {} game objects for seller with userId: {}", gameList.size(), userId);

    var toDtoGameList = gameList.stream()
        .map(GameObjectDto::toDto)
        .toList();

    return SellerProfileDto.builder()
        .userInfo(info)
        .gameObjects(toDtoGameList)
        .build();
  }



  /**
   * Registers a new user with optional profile photo.
   *
   * @param dto   user registration data
   * @param photo the profile picture
   * @return success message or error
   */
  @Override
  public void registerUser(RegisterUserDto dto, MultipartFile photo) {
    log.info("Registering new user with email: {}", dto.email);

    var checkifExists = userRepository.findByEmail(dto.email);
    if (checkifExists.isPresent()) {
      log.warn("Attempt to register with an existing email: {}", dto.email);
      throw new ResponseStatusException(BAD_REQUEST, UserConstMessages.EMAIL_ALREADY_EXISTS);
    }

    log.info("Creating new user with email: {}", dto.email);

    var newUser = User.builder()
        .createdAt(LocalDateTime.now())
        .firstName(dto.name)
        .lastName(dto.surname)
        .email(dto.email)
        .password(passwordEncoder.encode(dto.password))
        .isApprovedByAdmin(false)
        .role(UserRoleEnum.SELLER)
        .hasVerifiedEmail(false)
        .build();

    userRepository.save(newUser);
    log.info("New user created with ID: {}", newUser.getId());

    log.info("Attempting to save profile photo for user with ID: {}", newUser.getId());
    var savedPhoto = saveUserPictureLocal(newUser.getId(), photo);
    if (savedPhoto == null) {
      log.error("Failed to save profile photo for user with ID: {}", newUser.getId());
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, PICTURE_CANNOT_BE_SAVED);
    }

    savedPhoto.setUser(newUser);
    userPhotoRepository.save(savedPhoto);
    log.info("Profile photo saved for user with ID: {}", newUser.getId());

    sendRegistrationEmail(newUser);
    log.info("Registration email sent to user with email: {}", newUser.getEmail());
  }


  /**
   * Sends a confirmation email after registration with an activation token.
   *
   * @param user the user to send the token to
   */
  public void sendRegistrationEmail(User user) {
    log.info("Generating registration token for user with email: {}", user.getEmail());

    var generatedToken = UUID.randomUUID().toString();
    log.info("Generated token for user with email: {}", user.getEmail());

    try {
      emailService.sendConfirmationEmail(user.getEmail(), generatedToken);
      log.info("Confirmation email sent to user with email: {}", user.getEmail());
    } catch (Exception e) {
      log.error("Failed to send confirmation email to user with email: {}", user.getEmail(), e);
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to send confirmation email");
    }

    var token = Token.builder()
        .token(generatedToken)
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusHours(24))
        .user(user)
        .build();

    tokenRepository.save(token);
    log.info("Token saved in the database for user with email: {}", user.getEmail());
  }


  /**
   * Activates a user account via registration token.
   *
   * @param token the activation token
   */
  @Override
  @Transactional
  public void activateAccount(String token) {
    log.info("Activating account with token: {}", token);

    var savedToken = tokenRepository.findByToken(token);
    if (savedToken.isEmpty()) {
      log.warn("Token not found: {}", token);
      throw new ResponseStatusException(NOT_FOUND, TOKEN_NOT_FOUND);
    }

    var user = userRepository.findById(savedToken.get().getUser().getId())
        .orElseThrow(() -> {
          log.warn("User not found for token: {}", token);
          return new ResponseStatusException(NOT_FOUND, USER_NOT_FOUND);
        });

    if (user.isHasVerifiedEmail()) {
      log.info("User's account is already verified: {}", user.getEmail());
      throw new ResponseStatusException(BAD_REQUEST, ACCOUNT_IS_ALREADY_VERIFIED);
    }

    if (LocalDateTime.now().isAfter(savedToken.get().getExpiresAt())) {
      log.warn("Token expired for user: {}, token: {}", user.getEmail(), token);
      tokenRepository.deleteById(savedToken.get().getId());
      sendRegistrationEmail(user);
      log.info("New verification email sent to user: {}", user.getEmail());
      throw new ResponseStatusException(BAD_REQUEST, TOKEN_EXPIRED_NEW_TOKEN_SEND);
    }

    user.setHasVerifiedEmail(true);
    userRepository.save(user);
    log.info("User's account activated successfully: {}", user.getEmail());

    tokenRepository.deleteTokenById(savedToken.get().getId());
    log.info("Token with value {} deleted after successful account activation", token);
  }



  /**
   * Saves a profile picture to the local file system and returns a {@link UserPhoto} entity.
   *
   * @param userId  the user ID
   * @param picture the uploaded file
   * @return the photo metadata, or null if failed
   */

  private UserPhoto saveUserPictureLocal(int userId, MultipartFile picture) {
    String userFolderPath = uploadDir + File.separator + userId + File.separator + "Profile";
    File userFolder = new File(userFolderPath);


    if (!userFolder.exists()) {
      log.info("Creating directory for user {}: {}", userId, userFolderPath);
      if (userFolder.mkdirs()) {
        log.info("Directory created successfully: {}", userFolderPath);
      } else {
        log.error("Failed to create directory: {}", userFolderPath);
        return null;
      }
    }


    String uuid = UUID.randomUUID().toString();
    String modifiedFileName = uuid + picture.getOriginalFilename();
    String publicUrl = userId + File.separator + "Profile" + File.separator + modifiedFileName;
    String filePath = userFolderPath + File.separator + modifiedFileName;

    try {
      File savedFile = new File(filePath);
      picture.transferTo(savedFile);
      log.info("Picture saved successfully for user {} at path: {}", userId, filePath);

      return UserPhoto.builder()
          .url(publicUrl)
          .size(picture.getSize())
          .extension(picture.getContentType())
          .photoName(modifiedFileName)
          .build();
    } catch (IOException e) {
      log.error("Failed to save picture for user {}: {}", userId, e.getMessage());

    }

    return null;
  }


  /**
   * Sends a password recovery code to the user's email.
   *
   * @param email the user's email address
   */
  @Override
  public void sendRecoverCode(String email) {
    log.info("Attempting to find user by email: {}", email);

    var userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
      log.error("User not found with email: {}", email);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstMessages.USER_NOT_FOUND_WITH_EMAIL);
    }

    var user = userOpt.get();


    if (user.getPasswordRecoveryToken() != null) {
      log.warn("Password recovery token already sent for user: {}", email);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, UserConstMessages.PASSWORD_RECOVERY_TOKEN_ALREADY_SENT);
    }


    var generatedToken = UUID.randomUUID().toString();
    log.info("Generated recovery token for user {}: {}", email, generatedToken);

    var recoveryToken = PasswordRecoveryToken.builder()
        .token(generatedToken)
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusHours(24))
        .user(user)
        .build();


    pwdRecoveryTokenRepository.save(recoveryToken);
    log.info("Password recovery token saved for user {} with expiration at: {}", email, recoveryToken.getExpiresAt());


    emailService.sendRecoverLink(user.getEmail(), generatedToken);
    log.info("Password recovery email sent to: {}", email);
  }


  /**
   * Updates the password using a valid recovery token.
   *
   * @param dto the DTO containing token and new passwords
   */
  @Override
  public void updatePassword(RecoverPasswordDto dto) {
    log.info("Attempting to update password for token: {}", dto.getToken());

    var savedToken = pwdRecoveryTokenRepository.findByToken(dto.getToken())
        .orElseThrow(() -> {
          log.error("Invalid token: {}", dto.getToken());
          return new ResponseStatusException(BAD_REQUEST, INVALID_TOKEN);
        });


    if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
      log.warn("Token expired for token: {}", dto.getToken());
      pwdRecoveryTokenRepository.deleteById(savedToken.getId());
      throw new ResponseStatusException(BAD_REQUEST, TOKEN_EXPIRED);
    }

    var user = userRepository.findById(savedToken.getUser().getId())
        .orElseThrow(() -> {
          log.error("User not found for token: {}", dto.getToken());
          return new ResponseStatusException(NOT_FOUND, USER_NOT_FOUND);
        });

     if (!dto.getNewpassword().equals(dto.getPassword())) {
      log.error("Passwords do not match for user: {}", user.getEmail());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PASSWORD_DOES_NOT_MATCH);
    }

     user.setPassword(passwordEncoder.encode(dto.getPassword()));
    userRepository.save(user);
    log.info("Password successfully updated for user: {}", user.getEmail());

     pwdRecoveryTokenRepository.deleteById(savedToken.getId());
    log.info("Password recovery token deleted for token: {}", dto.getToken());
  }



  /**
   * Authenticates a user and returns a JWT if successful.
   *
   * @param loginDto login credentials.
   * @return JwtDto
   */
  public JwtDto login(LoginDto loginDto) {
    log.info("Attempting login for email: {}", loginDto.getEmail());

    var getUser = userRepository.findByEmail(loginDto.getEmail())
        .orElseThrow(() -> {
          log.error("User not found for email: {}", loginDto.getEmail());
          return new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NOT_FOUND);
        });

    if (!getUser.isApprovedByAdmin()) {
      log.warn("User is not approved by admin for email: {}", loginDto.getEmail());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NOT_APPROVED);
    }

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
      );
    } catch (Exception e) {
      log.error("Authentication failed for email: {}", loginDto.getEmail());
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTHENTICATION_FAILED);
    }

    String token = jwtFactory.generateToken(getUser);
    log.info("Successful login for email: {}. Token generated.", loginDto.getEmail());

    return new JwtDto(token, getUser.getRole().toString());
  }


  /**
   * Retrieves the user ID from an authentication object.
   *
   * @param authentication the auth object
   * @return the user ID or 0 if not found
   */
  public int retriaveLogedUserId(Authentication authentication) {
    log.info("Attempting to retrieve user ID for logged-in user with email: {}", authentication.getName());

    if (authentication.getName() != null) {
      var user = userRepository.findByEmail(authentication.getName());
      if (user.isPresent()) {
        log.info("User found for email: {}. User ID: {}", authentication.getName(), user.get().getId());
        return user.get().getId();
      } else {
        log.warn("No user found for email: {}", authentication.getName());
      }
    }

    log.error("Failed to retrieve user ID: Authentication name is null or user not found.");
    throw new ResponseStatusException(BAD_REQUEST, "User not found or invalid authentication");
  }


  /**
   * Checks whether the authenticated user has the ADMIN role.
   *
   * @param authentication the auth object
   * @return {@link IsAdminDto} indicating admin status
   */
  @Override
  public IsAdminDto checkIfAdmin(Authentication authentication) {
    String email = authentication.getName();
    log.info("Checking if user with email {} is an admin.", email);

    return userRepository.findByEmail(email)
        .map(user -> {
          boolean isAdmin = user.getRole() == UserRoleEnum.ADMIN;
          log.info("User with email {} is {}admin.", email, isAdmin ? "" : "not ");
          return new IsAdminDto(isAdmin);
        })
        .orElseThrow(() -> {
          log.error("No user found for email: {}", email);
          return new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NOT_FOUND);
        });
  }



  /**
   * clears securityContext and logs out user.
   *
   * @param request to invalidate
   */
  @Override
  public void logout(HttpServletRequest request) {
    try {
      String sessionId = request.getSession().getId();
      log.info("Logging out user with session ID: {}", sessionId);

      request.getSession().invalidate();
      SecurityContextHolder.clearContext();

      log.info("User with session ID: {} has been logged out successfully.", sessionId);
    } catch (Exception e) {
      log.error("Failed to log out user. Error: {}", e.getMessage(), e);
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, LOGGED_OUT_FAILED);
    }
  }


}
