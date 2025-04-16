package com.leverx.RatingSystemRest.Presentation.Controllers;


import com.leverx.RatingSystemRest.Business.Interfaces.CommentService;
import com.leverx.RatingSystemRest.Business.impl.GameObjectServiceImp;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/seller")
public class SellerController {
  private final UserServiceImpl userServiceImpl;
  private final GameObjectServiceImp gameObjectService;
  private final UserRepository userRepository;

  private final CommentService commentService;


  @GetMapping("/games")
  public ResponseEntity<List<GameObjectDto>> MyGames(Authentication authentication) throws Exception {

    var currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    if (currentUserId != 0) {

      return gameObjectService.getGameObjectsBySellerId(currentUserId);
    }

    return null;


  }

  @GetMapping("/reviews")
  public ResponseEntity<List<UserReviewsDto>> MyReviews(Authentication authentication) throws Exception {
    var currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    if (currentUserId != 0) {
      return commentService.getApprovedReviewsBySellerId(currentUserId);
    }
    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


  }

  @GetMapping("/info")
  public ResponseEntity<UserInfoDto> currenlysignedUserInfo(Authentication authentication) {
    var currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    if (currentUserId != 0) {
      return userServiceImpl.getUserInfoById(currentUserId);
    }
    return null;
  }


}
