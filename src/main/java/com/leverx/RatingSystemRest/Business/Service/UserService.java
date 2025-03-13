package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserPhotoRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.AdminNotApprovedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.DetailedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserReviewsDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPhotoRepository userPhotoRepository;
    private CommentService commentService;

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


      return   users.stream().map(user->DetailedUserDto.toDetailedUserDto(user)).toList();


    }


    public List<DetailedUserDto> GetDetailedRegisteredUsersByUsername(String username) {
        var users=userRepository.GetRegisteredSellerByUsername(username);
        if(users.isEmpty()) {
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


}
