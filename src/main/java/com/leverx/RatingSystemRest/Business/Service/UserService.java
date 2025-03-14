package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.CommentRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserPhotoRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder;


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


}
