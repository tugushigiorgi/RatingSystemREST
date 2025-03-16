package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Entities.*;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.CommentRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.TokenRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserPhotoRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service

public class UserService {
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;
    private PasswordEncoder passwordEncoder;
    private UserPhotoRepository userPhotoRepository;
    private EmailService emailService;
    private TokenRepository tokenRepository;
    public UserService(PasswordEncoder passwordEncoder, UserPhotoRepository userPhotoRepository, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userPhotoRepository = userPhotoRepository;
        this.userRepository = userRepository;
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
         var gameList=currentSeller.getGameObjects().stream().map(GameObjectDto::toDto).toList();

        return new ResponseEntity<>( SellerProfileDto.builder()
                    .userInfo(info)
                    .gameObjects(gameList).build(),HttpStatus.OK);

     }



     public ResponseEntity<String> registerUser(RegisterUserDto dto,MultipartFile photo) {

        if(dto.email.isEmpty() || dto.password.isEmpty() ||dto.confirmPassword.isEmpty()  || dto.name.isEmpty() || dto.surname.isEmpty()) {
            return new ResponseEntity<>("Please fill all fields", HttpStatus.BAD_REQUEST);
        }
        var checkifExists=userRepository.findByEmail(dto.email);
        if(checkifExists.isPresent()) {
             return new ResponseEntity<>("email already in use", HttpStatus.CONFLICT);
         }
         if(!Objects.equals(dto.password, dto.confirmPassword)){
             return new ResponseEntity<>("password does not match", HttpStatus.BAD_REQUEST);
         }

      var createUser=   User.builder()
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

        var savephoto= SaveUserPictureLocal( createUser.getId(),photo);
        if(savephoto!=null) {
            savephoto.setUser(createUser);
            userPhotoRepository.save(savephoto);
        }else{
            return new ResponseEntity<>("picture can not be saved", HttpStatus.INTERNAL_SERVER_ERROR);
        }
         SendRegistrationEmail(createUser);
    return new ResponseEntity<>("User registered successfully, Confirmation Code sent", HttpStatus.OK);
     }



     public void SendRegistrationEmail(User user) {

        String generatedtoken= UUID.randomUUID().toString();

        emailService.sendConfirmationEmail(user.getEmail(),generatedtoken);
         var token = Token.builder()
                 .token(generatedtoken)
                 .created_at(LocalDateTime.now())
                 .expires_at(LocalDateTime.now().plusHours(24))
                 .user(user)
                 .build();
         tokenRepository.save(token);

     }

     private ResponseEntity<String> ActivateAccount(String token, User usr) {

         var  savedToken = tokenRepository.findByToken(token);

            if(savedToken.isPresent()) {

         if (LocalDateTime.now().isAfter(savedToken.get().getExpires_at())) {
             return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
          }

         var user = userRepository.findById(savedToken.get().getUser().getId())
                 .orElseThrow(() -> new UsernameNotFoundException("User not found"));
         user.setHasVerifiedEmail(true);
         userRepository.save(user);

            tokenRepository.delete(savedToken.get());

     }
            return new ResponseEntity<>("token not found", HttpStatus.NOT_FOUND);
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
            return UserPhoto .builder()
                    .Url(publicUrl)
                    .size(picture.getSize())
                    .Extension(picture.getContentType())
                    .photoName(modifedFileName).build();

        } catch (IOException e) {


            e.printStackTrace();

        }
        return null;

    }


}
