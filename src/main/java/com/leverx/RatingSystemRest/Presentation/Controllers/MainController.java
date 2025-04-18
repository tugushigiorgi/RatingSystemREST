package com.leverx.RatingSystemRest.Presentation.Controllers;

import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.COMMENT_ADDED_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.UPDATED_SUCCESSFULLY_MESSAGE;

import com.leverx.RatingSystemRest.Business.Interfaces.CommentService;
import com.leverx.RatingSystemRest.Business.impl.EmailServiceImp;
import com.leverx.RatingSystemRest.Business.impl.GameObjectServiceImp;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.AddCommentDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.SellerProfileDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that provides the main public endpoints for the application.
 * Handles operations related to viewing seller profiles, searching game objects, and managing comments.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/main")
public class MainController {
  private UserServiceImpl userServiceImpl;
  private EmailServiceImp emailService;
  private GameObjectServiceImp gameObjectService;
  private CommentService commentService;

  /**
   * Retrieves a list of top-rated sellers in the system.
   *
   * @return ResponseEntity containing a list of top-rated sellers with their basic information
   */
  @GetMapping("/topselers")
  public ResponseEntity<List<UserInfoDto>> topRatedSellers() {
    var topSellers = userServiceImpl.getTopRatedSellers();
    return ResponseEntity.ok(topSellers);
  }

  /**
   * Retrieves detailed profile information for a specific seller.
   *
   * @param sellerId The ID of the seller whose profile information is requested
   * @return ResponseEntity containing the seller's profile data
   */
  @GetMapping("/SellerProfile/{sellerId}")
  public ResponseEntity<SellerProfileDto> getSellerProfileData(@PathVariable int sellerId) {
    SellerProfileDto profile = userServiceImpl.getSellerProfileById(sellerId);
    return ResponseEntity.ok(profile);
  }

  /**
   * Searches for game objects based on title and seller rating criteria.
   *
   * @param title Optional parameter to filter games by title
   * @param sellerRating Minimum seller rating to filter games by (defaults to 0)
   * @return ResponseEntity containing a list of game objects matching the search criteria
   */
  @GetMapping("/search")
  public ResponseEntity<List<GameObjectDto>> searchGames(
      @RequestParam(required = false) String title,
      @RequestParam(defaultValue = "0") int sellerRating) {

    var result = gameObjectService.searchGameObjects(sellerRating, title);
    return ResponseEntity.ok(result);
  }

  /**
   * Adds a new comment or review to the system.
   * The comment will be queued for approval before becoming visible.
   *
   * @param dto The DTO containing comment information
   * @return ResponseEntity with a success message upon adding the comment
   */
  @PostMapping("/addcomment")
  public ResponseEntity<String> add(@Valid @RequestBody AddCommentDto dto) {
    commentService.add(dto);
    return ResponseEntity.ok(COMMENT_ADDED_MESSAGE);
  }

  /**
   * Updates an existing comment with new content.
   * The updated comment may require re-approval depending on system settings.
   *
   * @param dto The DTO containing updated comment information
   * @return ResponseEntity with a success message upon updating the comment
   */
  @PutMapping("/comment")
  public ResponseEntity<String> updateComment(@Valid @RequestBody CommentUpdateDto dto) {
    commentService.update(dto);
    return ResponseEntity.ok(UPDATED_SUCCESSFULLY_MESSAGE);
  }
}