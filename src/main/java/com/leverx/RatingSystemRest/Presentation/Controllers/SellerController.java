package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.CommentService;
import com.leverx.RatingSystemRest.Business.GameObjectService;
import com.leverx.RatingSystemRest.Business.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/seller")
public class SellerController {
    private final UserService userService;
    private GameObjectService gameObjectService;
    private UserRepository userRepository;

    private CommentService commentService;


    @GetMapping("/games")
    public ResponseEntity<List<GameObjectDto>> MyGames(Authentication authentication) throws Exception {

        var currentUserId = userService.RetriaveLogedUserId(authentication);
        if (currentUserId != 0) {

            return gameObjectService.getGameObjectsBySellerId(currentUserId);
        }

        return null;


    }

    @GetMapping("/reviews")
    public ResponseEntity<List<UserReviewsDto>> MyReviews(Authentication authentication) throws Exception {
        var currentUserId = userService.RetriaveLogedUserId(authentication);
        if (currentUserId != 0) {
            return commentService.getApprovedReviewsBySellerId(currentUserId);
        }
        return new ResponseEntity<>( HttpStatus.BAD_REQUEST);


    }

    @GetMapping("/info")
    public ResponseEntity<UserInfoDto> currenlysignedUserInfo(Authentication authentication) {
        var currentUserId = userService.RetriaveLogedUserId(authentication);
        if (currentUserId != 0) {
            return userService.GetUserInfoById(currentUserId );
        }
        return null;
    }


}
