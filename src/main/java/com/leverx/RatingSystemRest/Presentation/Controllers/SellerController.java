package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.CommentService;
import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Presentation.Dto.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserReviewsDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/seller")
public class SellerController {
    private GameObjectService gameObjectService;

    private CommentService commentService;




    @GetMapping("/games")
    public List<GameObjectDto> MyGames() throws Exception {
       //TODO CURRENTLY LOGGED USER ID
     return   gameObjectService.getGameObjectsBySellerId(2);

   }

   @GetMapping("/reviews")
   public List<UserReviewsDto> MyReviews() throws Exception {

        //TODO currently logged user

       //TODO CHANGE TO APPROVED !!!!!!!!!!!!!!!!!!!!!!
      return commentService.getNotApprovedReviewsBySellerId(2);


   }







}
