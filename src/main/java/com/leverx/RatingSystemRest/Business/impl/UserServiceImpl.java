package com.leverx.RatingSystemRest.Business.impl;

import static com.leverx.RatingSystemRest.Business.ConstMessages.AuthConstMessages.LOGGED_OUT_FAILED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.FAILED_TO_DELETE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.FOLDER_AND_ITS_CONTENT_DELETED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.FOLDER_DOES_NOT_EXIST;
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
import static org.springframework.http.HttpStatus.OK;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    var getlist = userRepository.notApprovedSellersList();

    if (CollectionUtils.isEmpty(getlist)) {
      return emptyList();
    }

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
    var currentSeller = userRepository.findById(sellerId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND));

    if (!currentSeller.isHasVerifiedEmail()) {
      throw new ResponseStatusException(BAD_REQUEST, EMAIL_NOT_VERIFIED);
    }

    currentSeller.setApprovedByAdmin(true);
    userRepository.save(currentSeller);
  }


  /**
   * Retrieves a detailed list of approved seller users.
   *
   * @return list of {@link DetailedUserDto} or empty
   */
  @Override
  public List<DetailedUserDto> detailedRegisteredUsers() {
    var users = userRepository.approvedSellersList();

    if (CollectionUtils.isEmpty(users)) {
      return emptyList();
    }

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
    var users = userRepository.getRegisteredSellerByUsername(username);
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
    var user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, USER_NOT_FOUND));

    deleteUserFolderByUrl(uploadDir + File.separator + userId);
    userRepository.deleteById(userId);
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
        System.out.println(FOLDER_DOES_NOT_EXIST + folderUrl);
        return false;
      }
      deleteRecursively(folderPath);
      System.out.println(FOLDER_AND_ITS_CONTENT_DELETED + folderUrl);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
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
      files.sorted(Comparator.reverseOrder()).forEach(p -> {
        try {
          if (Files.isDirectory(p)) {
            Files.delete(p);
          } else {
            Files.delete(p);
          }
        } catch (IOException e) {
          System.err.println(FAILED_TO_DELETE + p);
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
    var user = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND));

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
    var user = userRepository.findById(currentUserId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, UserConstMessages.USER_NOT_FOUND));

    if (!dto.newPassword.equals(dto.repeatPassword)) {
      return UserConstMessages.NEW_AND_REPEAT_PASSWORD_DOES_NOT_MATCH;
    }

    if (passwordEncoder.encode(dto.oldPassword).equals(user.getPassword())) {
      return UserConstMessages.PASSWORD_IS_SAME;
    }

    if (!passwordEncoder.matches(dto.oldPassword, user.getPassword())) {
      return UserConstMessages.INCORRECT_PASSWORD;
    }

    user.setPassword(passwordEncoder.encode(dto.newPassword));
    userRepository.save(user);
    return UserConstMessages.PASSWORD_CHANGED;
  }


  /**
   * Fetches a list of top-rated seller users.
   *
   * @return list of {@link UserInfoDto} or empty
   */
  @Override
  public List<UserInfoDto> getTopRatedSellers() {
    var sellers = userRepository.findTop5RatedSellers();

    if (CollectionUtils.isEmpty(sellers)) {
      return emptyList();
    }

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
    var currentSeller = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, SELLER_NOT_FOUND));

    var info = UserInfoDto.toDto(currentSeller);
    var gameList = currentSeller.getGameObjects();

    if (CollectionUtils.isEmpty(gameList)) {
      return SellerProfileDto.builder()
          .userInfo(info)
          .gameObjects(emptyList())
          .build();

    }

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
    var checkifExists = userRepository.findByEmail(dto.email);
    if (checkifExists.isPresent()) {
      throw new ResponseStatusException(BAD_REQUEST, UserConstMessages.EMAIL_ALREADY_EXISTS);
    }

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

    var savedPhoto = saveUserPictureLocal(newUser.getId(), photo);
    if (savedPhoto == null) {
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, PICTURE_CANNOT_BE_SAVED);
    }

    savedPhoto.setUser(newUser);
    userPhotoRepository.save(savedPhoto);

    sendRegistrationEmail(newUser);
  }


  /**
   * Sends a confirmation email after registration with an activation token.
   *
   * @param user the user to send the token to
   */
  public void sendRegistrationEmail(User user) {

    var generatedToken = UUID.randomUUID().toString();
    emailService.sendConfirmationEmail(user.getEmail(), generatedToken);
    var token = Token.builder().token(generatedToken).createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusHours(24)).user(user).build();
    tokenRepository.save(token);

  }

  /**
   * Activates a user account via registration token.
   *
   * @param token the activation token
   */
  @Override
  @Transactional
  public void activateAccount(String token) {
    var savedToken = tokenRepository.findByToken(token);
    if (savedToken.isEmpty()) {
      throw new ResponseStatusException(NOT_FOUND, TOKEN_NOT_FOUND);
    }

    var user = userRepository.findById(savedToken.get().getUser().getId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, USER_NOT_FOUND));

    if (user.isHasVerifiedEmail()) {
      throw new ResponseStatusException(BAD_REQUEST, ACCOUNT_IS_ALREADY_VERIFIED);
    }

    if (LocalDateTime.now().isAfter(savedToken.get().getExpiresAt())) {
      tokenRepository.deleteById(savedToken.get().getId());
      sendRegistrationEmail(user);
      throw new ResponseStatusException(BAD_REQUEST, TOKEN_EXPIRED_NEW_TOKEN_SEND);
    }

    user.setHasVerifiedEmail(true);
    userRepository.save(user);
    tokenRepository.deleteTokenById(savedToken.get().getId());
  }


  /**
   * Saves a profile picture to the local file system and returns a {@link UserPhoto} entity.
   *
   * @param userid  the user ID
   * @param picture the uploaded file
   * @return the photo metadata, or null if failed
   */

  private UserPhoto saveUserPictureLocal(int userid, MultipartFile picture) {

    var userFolderPath = uploadDir + File.separator + userid + File.separator + "Profile";
    var userFolder = new File(userFolderPath);
    if (!userFolder.exists()) {
      userFolder.mkdirs();
    }
    var uuid = UUID.randomUUID();
    var modifiedFileName = uuid + picture.getOriginalFilename();
    var publicUrl = userid + File.separator + "Profile" + File.separator + modifiedFileName;
    var filePath = userFolderPath + File.separator + modifiedFileName;
    try {

      var savedFile = new File(filePath);
      picture.transferTo(savedFile);
      return UserPhoto.builder().url(publicUrl).size(picture.getSize()).extension(picture.getContentType()).photoName(modifiedFileName).build();

    } catch (IOException e) {


      e.printStackTrace();

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
    var userOpt = userRepository.findByEmail(email);
    if (userOpt.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstMessages.USER_NOT_FOUND_WITH_EMAIL);
    }

    var user = userOpt.get();

    if (user.getPasswordRecoveryToken() != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, UserConstMessages.PASSWORD_RECOVERY_TOKEN_ALREADY_SENT);
    }

    var generatedToken = UUID.randomUUID().toString();
    var recoveryToken = PasswordRecoveryToken.builder()
        .token(generatedToken)
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusHours(24))
        .user(user)
        .build();

    pwdRecoveryTokenRepository.save(recoveryToken);
    emailService.sendRecoverLink(user.getEmail(), generatedToken);
  }

  /**
   * Updates the password using a valid recovery token.
   *
   * @param dto the DTO containing token and new passwords
   */
  @Override
  public void updatePassword(RecoverPasswordDto dto) {
    var savedToken = pwdRecoveryTokenRepository.findByToken(dto.getToken())
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, INVALID_TOKEN));

    if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
      pwdRecoveryTokenRepository.deleteById(savedToken.getId());
      throw new ResponseStatusException(BAD_REQUEST, TOKEN_EXPIRED);
    }

    var user = userRepository.findById(savedToken.getUser().getId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, USER_NOT_FOUND));

    if (!dto.getNewpassword().equals(dto.getPassword())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PASSWORD_DOES_NOT_MATCH);
    }

    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    userRepository.save(user);
    pwdRecoveryTokenRepository.deletebyId(savedToken.getId());
  }


  /**
   * Authenticates a user and returns a JWT if successful.
   *
   * @param loginDto login credentials.
   * @return JwtDto
   */
  public JwtDto login(LoginDto loginDto) {
    var getUser = userRepository.findByEmail(loginDto.getEmail())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NOT_FOUND));

    if (!getUser.isApprovedByAdmin()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NOT_APPROVED);
    }

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
      );
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, AUTHENTICATION_FAILED);
    }

    String token = jwtFactory.generateToken(getUser);
    return new JwtDto(token, getUser.getRole().toString());
  }


  /**
   * Retrieves the user ID from an authentication object.
   *
   * @param authentication the auth object
   * @return the user ID or 0 if not found
   */
  public int retriaveLogedUserId(Authentication authentication) {
    if (authentication.getName() != null) {
      var user = userRepository.findByEmail(authentication.getName());
      if (user.isPresent()) {
        return user.get().getId();
      }

    }
    throw new ResponseStatusException(BAD_REQUEST);
  }

  /**
   * Checks whether the authenticated user has the ADMIN role.
   *
   * @param authentication the auth object
   * @return {@link IsAdminDto} indicating admin status
   */
  @Override
  public IsAdminDto checkIfAdmin(Authentication authentication) {
    return userRepository.findByEmail(authentication.getName())
        .map(user -> new IsAdminDto(user.getRole() == UserRoleEnum.ADMIN))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NOT_FOUND));
  }


  /**
   * clears securityContext and logs out user
   *
   * @param request to invalidate
   */
  @Override
  public void logout(HttpServletRequest request) {
    try {
      request.getSession().invalidate();
      SecurityContextHolder.clearContext();
    } catch (Exception e) {
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, LOGGED_OUT_FAILED);
    }
  }

}
