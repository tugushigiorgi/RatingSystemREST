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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that handles operations specific to authenticated sellers.
 * Provides endpoints for sellers to view their game listings, reviews, and personal information.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/seller")
public class SellerController {
  private final UserServiceImpl userServiceImpl;
  private final GameObjectServiceImp gameObjectService;
  private final UserRepository userRepository;
  private final CommentService commentService;

  /**
   * Retrieves all game objects listed by the currently authenticated seller.
   *
   * @param authentication The authentication object containing the seller's credentials
   * @return ResponseEntity containing a list of game objects owned by the seller
   */
  @GetMapping("/games")
  public ResponseEntity<List<GameObjectDto>> myGames(Authentication authentication) {
    int currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    List<GameObjectDto> games = gameObjectService.getGameObjectsBySellerId(currentUserId);

    return ResponseEntity.ok(games);
  }

  /**
   * Retrieves all approved reviews that have been submitted for the authenticated seller.
   *
   * @param authentication The authentication object containing the seller's credentials
   * @return ResponseEntity containing a list of approved reviews for the seller
   */
  @GetMapping("/reviews")
  public ResponseEntity<List<UserReviewsDto>> myReviews(Authentication authentication) {
    int currentUserId = userServiceImpl.retriaveLogedUserId(authentication);

    var reviews = commentService.getApprovedReviewsBySellerId(currentUserId);

    return ResponseEntity.ok(reviews);
  }

  /**
   * Retrieves profile information for the currently authenticated seller.
   *
   * @param authentication The authentication object containing the seller's credentials
   * @return ResponseEntity containing the seller's profile information
   */
  @GetMapping("/info")
  public ResponseEntity<UserInfoDto> currentlySignedUserInfo(Authentication authentication) {
    int currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    var userInfo = userServiceImpl.getUserInfoById(currentUserId);
    return ResponseEntity.ok(userInfo);
  }
}