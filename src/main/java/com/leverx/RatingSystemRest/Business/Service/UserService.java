package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Entities.*;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.*;
import com.leverx.RatingSystemRest.Infrastructure.Security.JwtFactory;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.jwtDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service

public class UserService {
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;
    private PasswordEncoder passwordEncoder;
    private UserPhotoRepository userPhotoRepository;
    private EmailService emailService;
    private TokenRepository tokenRepository;
    private passwordRecoveryTokenRepository pwdRecoveryTokenRepository;
    private JwtFactory jwtFactory;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserPhotoRepository userPhotoRepository, EmailService emailService, TokenRepository tokenRepository, passwordRecoveryTokenRepository pwdRecoveryTokenRepository, JwtFactory jwtFactory) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userPhotoRepository = userPhotoRepository;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
        this.pwdRecoveryTokenRepository = pwdRecoveryTokenRepository;
        this.jwtFactory = jwtFactory;
    }

    public List<AdminNotApprovedUserDto> getSellersRegistrationRequests() {
        return userRepository.notApprovedSellersList().stream().map(AdminNotApprovedUserDto::toDto).toList();

    }

    public ResponseEntity<String> AcceptSellerRegistrationRequest(int sellerId) {
        var getSeller = userRepository.findById(sellerId);
        if (getSeller.isEmpty()) {
            return new ResponseEntity<>("seller not found", HttpStatus.NOT_FOUND);
        }

        var currentSeller = getSeller.get();
        currentSeller.setApprovedByAdmin(true);
        userRepository.save(currentSeller);

        return new ResponseEntity<>("admin approved", HttpStatus.OK);
    }


    public List<DetailedUserDto> DetailedRegisteredUsers() {
        var users = userRepository.ApprovedSellersList();


        return users.stream().map(user -> DetailedUserDto.toDetailedUserDto(user)).toList();


    }


    public List<DetailedUserDto> GetDetailedRegisteredUsersByUsername(String username) {
        var users = userRepository.GetRegisteredSellerByUsername(username);
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return users.stream().map(DetailedUserDto::toDetailedUserDto).toList();


    }

    @Transactional
    public ResponseEntity<String> DeleteById(int userId) {

        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return new ResponseEntity<>("user not found", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
        return new ResponseEntity<>("user deleted", HttpStatus.OK);


    }


    public ResponseEntity<UserInfoDto> GetUserInfoById(int userId) {

        var getuser = userRepository.findById(userId);
        if (getuser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var toDto = UserInfoDto.toDto(getuser.get());

        return new ResponseEntity<>(toDto, HttpStatus.OK);


    }

    @Transactional
    public ResponseEntity<String> ChangePassword(int currentuserId, ChangePasswordDto dto) {

        var getuser = userRepository.findById(currentuserId).get();

        if (dto.oldPassword.length() < 6 || dto.newPassword.length() < 6 || dto.repeatPassword.length() < 6) {
            return new ResponseEntity<>("Passwords should contain at least 6 characters ", HttpStatus.BAD_REQUEST);


        }

        if (!Objects.equals(dto.newPassword, dto.repeatPassword)) {
            return new ResponseEntity<>("new and repeat passwords does not match", HttpStatus.BAD_REQUEST);
        }

        if (passwordEncoder.encode(dto.oldPassword).equals(getuser.getPassword())) {

            return new ResponseEntity<>("new password is same, submit different", HttpStatus.OK);
        }

        if (!passwordEncoder.matches(dto.oldPassword, getuser.getPassword())) {
            return new ResponseEntity<>("incorrect password", HttpStatus.BAD_REQUEST);
        }
        var newPassword = passwordEncoder.encode(dto.newPassword);
        getuser.setPassword(newPassword);
        userRepository.save(getuser);
        return new ResponseEntity<>("password changed", HttpStatus.OK);


    }


    public ResponseEntity<List<UserInfoDto>> getTopRatedSellers() {

        return new ResponseEntity<>(userRepository.findTop5RatedSellers().stream().map(UserInfoDto::toDto).toList(), HttpStatus.OK);

    }


    public ResponseEntity<SellerProfileDto> GetSellerProfileById(int userId) {

        var getuser = userRepository.findById(userId);
        if (getuser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        var currentSeller = getuser.get();

        var info = UserInfoDto.toDto(currentSeller);
        var gameList = currentSeller.getGameObjects().stream().map(GameObjectDto::toDto).toList();

        return new ResponseEntity<>(SellerProfileDto.builder()
                .userInfo(info)
                .gameObjects(gameList).build(), HttpStatus.OK);

    }


    public ResponseEntity<String> registerUser(RegisterUserDto dto, MultipartFile photo) {

        if (dto.email.isEmpty() || dto.password.isEmpty() || dto.name.isEmpty() || dto.surname.isEmpty()) {
            return new ResponseEntity<>("Please fill all fields", HttpStatus.BAD_REQUEST);
        }
        var checkifExists = userRepository.findByEmail(dto.email);
        if (checkifExists.isPresent()) {
            return new ResponseEntity<>("email already in use", HttpStatus.CONFLICT);
        }


        var createUser = User.builder()
                .created_at(LocalDateTime.now())
                .first_name(dto.name)
                .last_name(dto.surname)
                .email(dto.email)
                .password(passwordEncoder.encode(dto.password))
                .isApprovedByAdmin(false)

                .role(UserRoleEnum.SELLER)
                .HasVerifiedEmail(false)
                .build();

        userRepository.save(createUser);

        var savephoto = SaveUserPictureLocal(createUser.getId(), photo);
        if (savephoto != null) {
            savephoto.setUser(createUser);
            userPhotoRepository.save(savephoto);
        } else {
            return new ResponseEntity<>("picture can not be saved", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        SendRegistrationEmail(createUser);
        return new ResponseEntity<>("User registered successfully, Confirmation Code sent", HttpStatus.OK);
    }


    public void SendRegistrationEmail(User user) {

        String generatedtoken = UUID.randomUUID().toString();

        emailService.sendConfirmationEmail(user.getEmail(), generatedtoken);
        var token = Token.builder()
                .token(generatedtoken)
                .created_at(LocalDateTime.now())
                .expires_at(LocalDateTime.now().plusHours(24))
                .user(user)
                .build();
        tokenRepository.save(token);

    }

    @Transactional
    public ResponseEntity<String> ActivateAccount(String token) {

        var savedToken = tokenRepository.findByToken(token);
        if (savedToken.isEmpty()) {
            return new ResponseEntity<>("Token not found", HttpStatus.NOT_FOUND);
        }


        var user = userRepository.findById(savedToken.get().getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if (LocalDateTime.now().isAfter(savedToken.get().getExpires_at())) {

            tokenRepository.deleteById(savedToken.get().getId());
            SendRegistrationEmail(user);

            return new ResponseEntity<>("token expired, New email sent to email", HttpStatus.BAD_REQUEST);
        }
        user.setHasVerifiedEmail(true);

        userRepository.save(user);
        tokenRepository.deleteTokenById(savedToken.get().getId());


        return new ResponseEntity<>("Succesfully Enabled", HttpStatus.OK);
    }


    private UserPhoto SaveUserPictureLocal(int userid, MultipartFile picture) {

        String userFolderPath = uploadDir + File.separator + userid + File.separator + "Profile";


        File userFolder = new File(userFolderPath);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
        UUID uuid = UUID.randomUUID();
        String modifedFileName = uuid + picture.getOriginalFilename();
        String publicUrl = userid + File.separator + "Profile" + File.separator + modifedFileName;
        String filePath = userFolderPath + File.separator + modifedFileName;
        try {

            File savedFile = new File(filePath);
            picture.transferTo(savedFile);
            return UserPhoto.builder()
                    .Url(publicUrl)
                    .size(picture.getSize())
                    .Extension(picture.getContentType())
                    .photoName(modifedFileName).build();

        } catch (IOException e) {


            e.printStackTrace();

        }
        return null;

    }


    public ResponseEntity<String> SendRecoverCode(String email) {
        var getuser = userRepository.findByEmail(email);
        if (getuser.isEmpty()) {
            return new ResponseEntity<>("User  not found with email addresss", HttpStatus.NOT_FOUND);
        }
        var currentuser = getuser.get();
        String generatedtoken = UUID.randomUUID().toString();

        emailService.sendConfirmationEmail(currentuser.getEmail(), generatedtoken);
        var token = PasswordRecoveryToken.builder()
                .token(generatedtoken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusHours(24))
                .user(currentuser)
                .build();
        pwdRecoveryTokenRepository.save(token);
        emailService.sendRecoverLink(currentuser.getEmail(), generatedtoken);


        return new ResponseEntity<>("Email sent successfully", HttpStatus.OK);


    }

    public ResponseEntity<String> updatePassword(RecoverPasswordDto dto) {
        System.out.println(dto);

        var savedToken = pwdRecoveryTokenRepository.findByToken(dto.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));


        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            pwdRecoveryTokenRepository.deleteById(savedToken.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired");
        }


        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if (!dto.getNewpassword().equals(dto.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }


        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);


        pwdRecoveryTokenRepository.DeleteByID(savedToken.getId());

        return ResponseEntity.ok("Password updated successfully");
    }


    public ResponseEntity<jwtDto> Login(AuthenticationManager authenticationManager, Logindto logindto) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(logindto.getEmail(), logindto.getPassword()));


            Optional<User> userDetails = userRepository.findByEmail(logindto.getEmail());

            if (userDetails.isPresent()) {


                jwtFactory = new JwtFactory();

            var token =jwtFactory.generateToken(userDetails.get());
            var dto=new jwtDto(token,userDetails.get().getRole().toString());
             return ResponseEntity.ok(dto);
            }
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);

    }


}
