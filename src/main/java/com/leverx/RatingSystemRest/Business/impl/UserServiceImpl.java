package com.leverx.RatingSystemRest.Business.impl;

import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.FAILED_TO_DELETE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.FOLDER_AND_ITS_CONTENT_DELETED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.FOLDER_DOES_NOT_EXIST;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.PICTURE_CANNOT_BE_SAVED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.ACCOUNT_IS_ALREADY_VERIFIED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.AUTHENTICATION_FAILED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.EMAIL_ALREADY_EXISTS;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.EMAIL_SENT_SUCCESSFULLY;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.INCORRECT_PASSWORD;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.NEW_AND_REPEAT_PASSWORD_DOES_NOT_MATCH;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.PASSWORD_CHANGED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.PASSWORD_IS_SAME;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.PASSWORD_RECOVERY_TOKEN_ALREADY_SENT;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.PASSWORD_UPDATED_SUCCESSFULLY;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.SUCCESSFULLY_APPROVED_SELLER_REGISTRATION;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.SUCCESSFULLY_ENABLED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.TOKEN_EXPIRED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.TOKEN_EXPIRED_NEW_TOKEN_SEND;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.TOKEN_NOT_FOUND;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.USER_DELETED_SUCCESSFULLY;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.USER_NOT_FOUND;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.USER_NOT_FOUND_WITH_EMAIL;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.USER_REGISTERED_SUCCESSFULLY;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.IsAdminDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.JwtDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.AdminNotApprovedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.DetailedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.LoginDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.RegisterUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.SellerProfileDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
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


  /**
   * Retrieves all pending seller registration requests.
   *
   * @return a list of users pending approval as {@link AdminNotApprovedUserDto}.
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
   * @return true if successfully accept, else throws Error;
   */
  public boolean acceptSellerRegistrationRequest(int sellerId) {
    var currentSeller = userRepository.findById(sellerId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, UserConstMessages.SELLER_NOT_FOUND));

    if (!currentSeller.isHasVerifiedEmail()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller's email is not verified.");
    }

    currentSeller.setApprovedByAdmin(true);
    userRepository.save(currentSeller);
    return true;
  }

  /**
   * Retrieves a detailed list of approved seller users.
   *
   * @return list of {@link DetailedUserDto} or empty.
   */
  @Override
  public List<DetailedUserDto> detailedRegisteredUsers() {
    var users = userRepository.approvedSellersList();
    if (CollectionUtils.isEmpty(users)) {
      return emptyList();
    }
    return users.stream()
        .map(DetailedUserDto::toDetailedUserDto)
        .collect(toList());
  }

  /**
   * Retrieves detailed seller information filtered by username.
   *
   * @param username the seller's username
   * @return a list of matching sellers
   */
  public List<DetailedUserDto> getDetailedRegisteredUsersByUsername(String username) {
    var users = userRepository.getRegisteredSellerByUsername(username);
    if (users.isEmpty()) {
      return emptyList();
    }
    return users.stream()
        .map(DetailedUserDto::toDetailedUserDto)
        .collect(toList());
  }

  /**
   * Deletes a user by ID along with their profile picture folder.
   *
   * @param userId the ID of the user to delete
   */
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
   * @return {@link UserInfoDto} or NOT_FOUND if user is missing
   */
  public ResponseEntity<UserInfoDto> getUserInfoById(int userId) {
    var getuser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstMessages.SELLER_NOT_FOUND));
    var toDto = UserInfoDto.toDto(getuser);
    return new ResponseEntity<>(toDto, OK);
  }


  /**
   * Allows a user to change their password.
   *
   * @param currentuserId the ID of the user requesting the change
   * @param dto           the DTO containing old and new password
   * @return success or error message
   */
  public ResponseEntity<String> changePassword(int currentuserId, ChangePasswordDto dto) {

    var getuser = userRepository.findById(currentuserId).orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, UserConstMessages.USER_NOT_FOUND));
    if (!Objects.equals(dto.newPassword, dto.repeatPassword)) {

      return new ResponseEntity<>(NEW_AND_REPEAT_PASSWORD_DOES_NOT_MATCH, BAD_REQUEST);
    }
    if (passwordEncoder.encode(dto.oldPassword).equals(getuser.getPassword())) {

      return new ResponseEntity<>(PASSWORD_IS_SAME, OK);
    }

    if (!passwordEncoder.matches(dto.oldPassword, getuser.getPassword())) {
      return new ResponseEntity<>(INCORRECT_PASSWORD, BAD_REQUEST);
    }
    var newPassword = passwordEncoder.encode(dto.newPassword);
    getuser.setPassword(newPassword);
    userRepository.save(getuser);
    return new ResponseEntity<>(PASSWORD_CHANGED, OK);


  }

  /**
   * Fetches a list of top-rated seller users.
   *
   * @return list of {@link UserInfoDto} or NO_CONTENT if none
   */
  public ResponseEntity<List<UserInfoDto>> getTopRatedSellers() {

    var getlist = userRepository.findTop5RatedSellers();

    if (CollectionUtils.isEmpty(getlist)) {
      return new ResponseEntity<>(NO_CONTENT);
    }
    var toDtoList = getlist.stream().map(UserInfoDto::toDto).toList();
    return new ResponseEntity<>(toDtoList, OK);

  }

  /**
   * Retrieves a seller's full profile by user ID, including game objects.
   *
   * @param userId the seller's user ID
   * @return {@link SellerProfileDto} or NO_CONTENT if no games
   */
  public ResponseEntity<SellerProfileDto> getSellerProfileById(int userId) {

    var currentSeller = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstMessages.SELLER_NOT_FOUND));
    var info = UserInfoDto.toDto(currentSeller);
    var gameList = currentSeller.getGameObjects();
    if (CollectionUtils.isEmpty(gameList)) {
      return new ResponseEntity<>(NO_CONTENT);
    }
    var toDtoGameList = gameList.stream().map(GameObjectDto::toDto).toList();

    return new ResponseEntity<>(SellerProfileDto.builder().userInfo(info).gameObjects(toDtoGameList).build(), OK);

  }


  /**
   * Registers a new user with optional profile photo.
   *
   * @param dto   user registration data
   * @param photo the profile picture
   * @return success message or error
   */
  public ResponseEntity<String> registerUser(RegisterUserDto dto, MultipartFile photo) {

    var checkifExists = userRepository.findByEmail(dto.email);
    if (checkifExists.isPresent()) {
      return new ResponseEntity<>(EMAIL_ALREADY_EXISTS, BAD_REQUEST);
    }
    var createUser = User.builder().createdAt(LocalDateTime.now()).firstName(dto.name).lastName(dto.surname).email(dto.email).password(passwordEncoder.encode(dto.password)).isApprovedByAdmin(false).role(UserRoleEnum.SELLER).hasVerifiedEmail(false).build();

    userRepository.save(createUser);
    var savephoto = saveUserPictureLocal(createUser.getId(), photo);
    if (savephoto != null) {
      savephoto.setUser(createUser);
      userPhotoRepository.save(savephoto);
    } else {
      return new ResponseEntity<>(PICTURE_CANNOT_BE_SAVED, INTERNAL_SERVER_ERROR);
    }
    sendRegistrationEmail(createUser);
    return new ResponseEntity<>(USER_REGISTERED_SUCCESSFULLY, OK);
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
   * @return success or error response depending on validity and expiration
   */
  @Transactional
  public ResponseEntity<String> activateAccount(String token) {

    var savedToken = tokenRepository.findByToken(token);
    if (savedToken.isEmpty()) {
      return new ResponseEntity<>(TOKEN_NOT_FOUND, NOT_FOUND);
    }
    var user = userRepository.findById(savedToken.get().getUser().getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

    if (user.isHasVerifiedEmail()) {
      return new ResponseEntity<>(ACCOUNT_IS_ALREADY_VERIFIED, BAD_REQUEST);
    }

    if (LocalDateTime.now().isAfter(savedToken.get().getExpiresAt())) {

      tokenRepository.deleteById(savedToken.get().getId());
      sendRegistrationEmail(user);

      return new ResponseEntity<>(TOKEN_EXPIRED_NEW_TOKEN_SEND, BAD_REQUEST);
    }
    user.setHasVerifiedEmail(true);
    userRepository.save(user);
    tokenRepository.deleteTokenById(savedToken.get().getId());

    return new ResponseEntity<>(SUCCESSFULLY_ENABLED, OK);
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
   * @return success or error message
   */
  public ResponseEntity<String> sendRecoverCode(String email) {
    var getuser = userRepository.findByEmail(email);
    if (getuser.isEmpty()) {
      return new ResponseEntity<>(USER_NOT_FOUND_WITH_EMAIL, NOT_FOUND);
    }

    if (getuser.get().getPasswordRecoveryToken() != null) {

      return new ResponseEntity<>(PASSWORD_RECOVERY_TOKEN_ALREADY_SENT, BAD_REQUEST);
    }

    var currentuser = getuser.get();
    var generatedtoken = UUID.randomUUID().toString();
    var token = PasswordRecoveryToken.
        builder()
        .token(generatedtoken)
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusHours(24))
        .user(currentuser)
        .build();
    pwdRecoveryTokenRepository.save(token);
    emailService.sendRecoverLink(currentuser.getEmail(), generatedtoken);
    return new ResponseEntity<>(EMAIL_SENT_SUCCESSFULLY, OK);
  }

  /**
   * Updates the password using a valid recovery token.
   *
   * @param dto the DTO containing token and new passwords
   * @return success or error response
   */
  public ResponseEntity<String> updatePassword(RecoverPasswordDto dto) {

    var savedToken = pwdRecoveryTokenRepository.findByToken(dto.getToken()).orElseThrow(() -> new IllegalArgumentException(UserConstMessages.INVALID_TOKEN));
    if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
      pwdRecoveryTokenRepository.deleteById(savedToken.getId());
      return ResponseEntity.status(BAD_REQUEST).body(TOKEN_EXPIRED);
    }
    var user = userRepository.findById(savedToken.getUser().getId()).orElseThrow(() -> new UsernameNotFoundException(UserConstMessages.USER_NOT_FOUND));

    if (!dto.getNewpassword().equals(dto.getPassword())) {
      return ResponseEntity.status(BAD_REQUEST).body(UserConstMessages.PASSWORD_DOES_NOT_MATCH);
    }
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    userRepository.save(user);
    pwdRecoveryTokenRepository.deletebyId(savedToken.getId());

    return ResponseEntity.ok(PASSWORD_UPDATED_SUCCESSFULLY);
  }

  /**
   * Authenticates a user and returns a JWT if successful.
   *
   * @param authenticationManager the auth manager
   * @param logindto              login credentials
   * @return JWT token with role or UNAUTHORIZED
   */
  public ResponseEntity<JwtDto> login(AuthenticationManager authenticationManager, LoginDto logindto) {

    try {
      var getuser = userRepository.findByEmail(logindto.getEmail());
      if (getuser.isPresent()) {
        if (!getuser.get().isApprovedByAdmin) {
          return ResponseEntity.status(BAD_REQUEST).body(null);
        }
      }
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logindto.getEmail(), logindto.getPassword()));
      var token = jwtFactory.generateToken(getuser.get());
      var dto = new JwtDto(token, getuser.get().getRole().toString());
      return ResponseEntity.ok(dto);

    } catch (Exception e) {
      System.out.println(AUTHENTICATION_FAILED + " " + e.getMessage());
      return ResponseEntity.status(UNAUTHORIZED).body(null);
    }

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
    return 0;
  }

  /**
   * Checks whether the authenticated user has the ADMIN role.
   *
   * @param authentication the auth object
   * @return {@link IsAdminDto} indicating admin status
   */
  public ResponseEntity<IsAdminDto> checkifAdmin(Authentication authentication) {
    var getusr = userRepository.findByEmail(authentication.getName());
    if (getusr.isPresent()) {
      if (getusr.get().getRole().equals(UserRoleEnum.ADMIN)) {
        return new ResponseEntity(new IsAdminDto(true), OK);
      }

      return new ResponseEntity<>(new IsAdminDto(false), OK);

    }
    return ResponseEntity.status(BAD_REQUEST).body(null);

  }


}
