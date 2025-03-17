package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.CommentService;
import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import lombok.AllArgsConstructor;
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
    public List<GameObjectDto> MyGames(Authentication authentication) throws Exception {
        if(authentication.getName()!=null) {
            var user = userRepository.findByEmail(authentication.getName());

            return   gameObjectService.getGameObjectsBySellerId(user.get().getId());
        }
        return null;


   }

   @GetMapping("/reviews")
   public List<UserReviewsDto> MyReviews(Authentication authentication) throws Exception {


       if(authentication.getName()!=null) {
           var user = userRepository.findByEmail(authentication.getName());

           return commentService.getApprovedReviewsBySellerId(user.get().getId());
       }

      return null;


   }

   @GetMapping("/info")
   public ResponseEntity<UserInfoDto> currenlysignedUserInfo(Authentication authentication)   {
       if(authentication.getName()!=null) {
           var user = userRepository.findByEmail(authentication.getName());


           return userService.GetUserInfoById(user.get().getId());
       }
       return null;


   }






}
