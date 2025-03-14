package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.CommentService;
import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserInfoDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserReviewsDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public List<GameObjectDto> MyGames() throws Exception {
       //TODO CURRENTLY LOGGED USER ID
     return   gameObjectService.getGameObjectsBySellerId(2);

   }

   @GetMapping("/reviews")
   public List<UserReviewsDto> MyReviews() throws Exception {

        //TODO currently logged user


      return commentService.getApprovedReviewsBySellerId(2);


   }

   @GetMapping("/info")
   public ResponseEntity<UserInfoDto> currenlysignedUserInfo()   {


        //TODO currently logged user
       return userService.GetUserInfoById(2);



   }






}
