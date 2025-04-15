package com.leverx.RatingSystemRest.Business.Interfaces;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.isAdminDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {


    ResponseEntity<List<AdminNotApprovedUserDto>> getSellersRegistrationRequests();
     ResponseEntity<String> acceptSellerRegistrationRequest(int sellerId);
    ResponseEntity<List<DetailedUserDto>> detailedRegisteredUsers();
     ResponseEntity<List<DetailedUserDto>> getDetailedRegisteredUsersByUsername(String username);
    ResponseEntity<String> deleteById(int userId) ;
     ResponseEntity<UserInfoDto> getUserInfoById(int userId);
   ResponseEntity<String> changePassword(int currentuserId, ChangePasswordDto dto);
    ResponseEntity<List<UserInfoDto>> getTopRatedSellers();
    ResponseEntity<SellerProfileDto> getSellerProfileById(int userId);
    ResponseEntity<String> registerUser(RegisterUserDto dto, MultipartFile photo);
    void sendRegistrationEmail(User user);
     int retriaveLogedUserId(Authentication authentication) ;
    ResponseEntity<isAdminDto> checkifAdmin(Authentication authentication);




}
