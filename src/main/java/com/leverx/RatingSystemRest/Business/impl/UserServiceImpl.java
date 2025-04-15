package com.leverx.RatingSystemRest.Business.impl;

import com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages;
import com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages;
import com.leverx.RatingSystemRest.Business.Interfaces.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Entities.*;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.*;
import com.leverx.RatingSystemRest.Infrastructure.Security.JwtFactory;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.isAdminDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.jwtDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.*;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.*;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.*;
import static org.springframework.http.HttpStatus.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final PasswordEncoder passwordEncoder;
    private final UserPhotoRepository userPhotoRepository;
    private final EmailServiceImp emailService;
    private final TokenRepository tokenRepository;
    private final passwordRecoveryTokenRepository pwdRecoveryTokenRepository;
    private final JwtFactory jwtFactory;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserPhotoRepository userPhotoRepository, EmailServiceImp emailService, TokenRepository tokenRepository, passwordRecoveryTokenRepository pwdRecoveryTokenRepository, JwtFactory jwtFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userPhotoRepository = userPhotoRepository;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.pwdRecoveryTokenRepository = pwdRecoveryTokenRepository;
        this.jwtFactory = jwtFactory;
    }

    public ResponseEntity<List<AdminNotApprovedUserDto>> getSellersRegistrationRequests() {

        var getlist = userRepository.notApprovedSellersList();

        if (CollectionUtils.isEmpty(getlist)) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        // TODO: write code base on this example:
        var mapToDtoList = getlist.stream()
                .map(AdminNotApprovedUserDto::toDto)
                .toList();
        return new ResponseEntity<>(mapToDtoList, OK);

    }

    public ResponseEntity<String> acceptSellerRegistrationRequest(int sellerId) {
        var currentSeller = userRepository.findById(sellerId)
                // TODO: try to check all examples when we should use static import
                // TODO: chekc how to replace it to constant(messages) + additional info about seller
//                // String.format("This is the string: %s","test")
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, UserConstMessages.SELLER_NOT_FOUND));

        if (!currentSeller.isHasVerifiedEmail()) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
        currentSeller.setApprovedByAdmin(true);
        userRepository.save(currentSeller);

        return new ResponseEntity<>(SUCCESSFULLY_APPROVED_SELLER_REGISTRATION, OK);
    }


    public ResponseEntity<List<DetailedUserDto>> detailedRegisteredUsers() {
        var users = userRepository.ApprovedSellersList();

        if (CollectionUtils.isEmpty(users)) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        var toDtoList = users.stream().map(DetailedUserDto::toDetailedUserDto).toList();
        return new ResponseEntity<>(toDtoList, OK);
    }


    public ResponseEntity<List<DetailedUserDto>> getDetailedRegisteredUsersByUsername(String username) {
        var users = userRepository.GetRegisteredSellerByUsername(username);
        if (users.isEmpty()) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        var toDtoList = users.stream().map(DetailedUserDto::toDetailedUserDto).toList();
        return new ResponseEntity<>(toDtoList, OK);
    }

    @Transactional
    public ResponseEntity<String> deleteById(int userId) {

        var user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "User not found"));

        deleteUserFolderByUrl(uploadDir + File.separator + userId);
        userRepository.deleteById(userId);

        return new ResponseEntity<>(USER_DELETED_SUCCESSFULLY, OK);
    }


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


    public ResponseEntity<UserInfoDto> getUserInfoById(int userId) {
        var getuser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, UserConstMessages.SELLER_NOT_FOUND));
        var toDto = UserInfoDto.toDto(getuser);
        return new ResponseEntity<>(toDto, OK);
    }


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


    public ResponseEntity<List<UserInfoDto>> getTopRatedSellers() {

        var getlist = userRepository.findTop5RatedSellers();

        if (CollectionUtils.isEmpty(getlist)) {
            return new ResponseEntity<>(NO_CONTENT);
        }
        var toDtoList = getlist.stream()
                .map(UserInfoDto::toDto)
                .toList();
        return new ResponseEntity<>(toDtoList, OK);

    }


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


    public ResponseEntity<String> registerUser(RegisterUserDto dto, MultipartFile photo) {


        var checkifExists = userRepository.findByEmail(dto.email);

        if (checkifExists.isPresent()) {
            return new ResponseEntity<>(EMAIL_ALREADY_EXISTS, BAD_REQUEST);
        }
        var createUser = User.builder().created_at(LocalDateTime.now()).first_name(dto.name).last_name(dto.surname).email(dto.email).password(passwordEncoder.encode(dto.password)).isApprovedByAdmin(false).role(UserRoleEnum.SELLER).HasVerifiedEmail(false).build();

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


    public void sendRegistrationEmail(User user) {

        var generatedtoken = UUID.randomUUID().toString();

        emailService.sendConfirmationEmail(user.getEmail(), generatedtoken);
        var token = Token.builder().token(generatedtoken).created_at(LocalDateTime.now()).expires_at(LocalDateTime.now().plusHours(24)).user(user).build();
        tokenRepository.save(token);

    }

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

        if (LocalDateTime.now().isAfter(savedToken.get().getExpires_at())) {

            tokenRepository.deleteById(savedToken.get().getId());
            sendRegistrationEmail(user);

            return new ResponseEntity<>(TOKEN_EXPIRED_NEW_TOKEN_SEND, BAD_REQUEST);
        }
        user.setHasVerifiedEmail(true);

        userRepository.save(user);
        tokenRepository.deleteTokenById(savedToken.get().getId());


        return new ResponseEntity<>(SUCCESSFULLY_ENABLED, OK);
    }


    private UserPhoto saveUserPictureLocal(int userid, MultipartFile picture) {

        var userFolderPath = uploadDir + File.separator + userid + File.separator + "Profile";


        var userFolder = new File(userFolderPath);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
        var uuid = UUID.randomUUID();
        var modifedFileName = uuid + picture.getOriginalFilename();
        var publicUrl = userid + File.separator + "Profile" + File.separator + modifedFileName;
        var filePath = userFolderPath + File.separator + modifedFileName;
        try {

            var savedFile = new File(filePath);
            picture.transferTo(savedFile);
            return UserPhoto.builder().Url(publicUrl).size(picture.getSize()).Extension(picture.getContentType()).photoName(modifedFileName).build();

        } catch (IOException e) {


            e.printStackTrace();

        }
        return null;

    }


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
        var token = PasswordRecoveryToken.builder().token(generatedtoken).createdAt(LocalDateTime.now()).expiresAt(LocalDateTime.now().plusHours(24)).user(currentuser).build();
        pwdRecoveryTokenRepository.save(token);
        emailService.sendRecoverLink(currentuser.getEmail(), generatedtoken);


        return new ResponseEntity<>(EMAIL_SENT_SUCCESSFULLY, OK);


    }

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


        pwdRecoveryTokenRepository.DeleteByID(savedToken.getId());

        return ResponseEntity.ok(PASSWORD_UPDATED_SUCCESSFULLY);
    }


    public ResponseEntity<jwtDto> login(AuthenticationManager authenticationManager, Logindto logindto) {

        try {
            var getuser = userRepository.findByEmail(logindto.getEmail());
            if (getuser.isPresent()) {
                if (!getuser.get().isApprovedByAdmin) {
                    return ResponseEntity.status(BAD_REQUEST).body(null);
                }
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(logindto.getEmail(), logindto.getPassword()));
            var jwtFactory = new JwtFactory();
            var token = jwtFactory.generateToken(getuser.get());
            var dto = new jwtDto(token, getuser.get().getRole().toString());
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            System.out.println(AUTHENTICATION_FAILED + " " + e.getMessage());
            return ResponseEntity.status(UNAUTHORIZED).body(null);
        }

    }


    public int retriaveLogedUserId(Authentication authentication) {
        if (authentication.getName() != null) {
            var user = userRepository.findByEmail(authentication.getName());

            if (user.isPresent()) {
                return user.get().getId();
            }

        }

        return 0;
    }


    public ResponseEntity<isAdminDto> checkifAdmin(Authentication authentication) {
        var getusr = userRepository.findByEmail(authentication.getName());
        if (getusr.isPresent()) {
            if (getusr.get().getRole().equals(UserRoleEnum.ADMIN)) {
                return new ResponseEntity(new isAdminDto(true), OK);
            }

            return new ResponseEntity<>(new isAdminDto(false), OK);

        }
        return ResponseEntity.status(BAD_REQUEST).body(null);

    }


}
