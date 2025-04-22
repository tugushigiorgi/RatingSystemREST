package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages;
import com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages;
import com.leverx.RatingSystemRest.Business.Interfaces.CommentService;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.AdminNotApprovedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.DetailedUserDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that handles admin-related operations for users and comments.
 * Provides endpoints for managing user registration requests and comment approvals.
 */
@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {

  private final UserServiceImpl userServiceImpl;
  private final CommentService commentService;

  /**
   * Retrieves all pending seller registration requests.
   *
   * @return ResponseEntity containing a list of unapproved seller registration requests.
   *         Returns a 204 No Content response if there are no pending requests.
   */
  @GetMapping("/users/requests")
  public ResponseEntity<List<AdminNotApprovedUserDto>> registrationRequestsList() {
    var requests = userServiceImpl.getSellersRegistrationRequests();

    if (CollectionUtils.isEmpty(requests)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(requests);
  }

  /**
   * Approves a pending seller registration request.
   *
   * @param id The ID of the seller registration request to approve
   * @return ResponseEntity with a success message upon approval
   */
  @PostMapping("/user/approve/{id}")
  public ResponseEntity<String> approveRegistrationRequest(@PathVariable int id) {
    userServiceImpl.acceptSellerRegistrationRequest(id);
    return ResponseEntity.ok(UserConstMessages.SUCCESSFULLY_APPROVED_SELLER_REGISTRATION);
  }

  /**
   * Retrieves all pending user review requests that require admin approval.
   *
   * @return ResponseEntity containing a list of unapproved user reviews.
   *         Returns a 204 No Content response if there are no pending reviews.
   */
  @GetMapping("/comments/requests")
  public ResponseEntity<List<UserReviewsDto>> reviewsRequestsList() {
    List<UserReviewsDto> reviews = commentService.getAllNotApprovedReviews();
    if (CollectionUtils.isEmpty(reviews)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(reviews);
  }

  /**
   * Approves a pending user review.
   *
   * @param id The ID of the review to approve
   * @return ResponseEntity with a success message upon approval
   */
  @PostMapping("/comments/approve/{id}")
  public ResponseEntity<String> approveReviewRequest(@PathVariable int id) {
    commentService.approveUserReview(id);
    return ResponseEntity.ok(CommentConstMessages.COMMENT_APPROVED_MESSAGE);
  }

  /**
   * Declines and removes a pending user review.
   *
   * @param id The ID of the review to decline and delete
   * @return ResponseEntity with a success message upon deletion
   */
  @DeleteMapping("/comments/decline/{id}")
  public ResponseEntity<String> declineReviewRequest(@PathVariable int id) {
    commentService.declineUserReview(id);
    return ResponseEntity.ok(CommentConstMessages.COMMENT_DELETED_MESSAGE);
  }

  /**
   * Retrieves a detailed list of all registered users in the system.
   *
   * @return ResponseEntity containing a list of detailed user information.
   *         Returns a 204 No Content response if there are no registered users.
   */
  @GetMapping("/user/registeredusers")
  public ResponseEntity<List<DetailedUserDto>> getDetailedRegisteredUserList() {
    var users = userServiceImpl.detailedRegisteredUsers();

    if (CollectionUtils.isEmpty(users)) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(users);
  }

  /**
   * Searches and retrieves registered users by their username.
   *
   * @param username The username to search for
   * @return ResponseEntity containing a list of detailed user information matching the username.
   *         Returns a 204 No Content response if no matching users are found.
   */
  @GetMapping("/user/registeredusers/{username}")
  public ResponseEntity<List<DetailedUserDto>> getDetailedRegisteredUserListByUsername(@PathVariable String username) {
    var users = userServiceImpl.getDetailedRegisteredUsersByUsername(username);

    return ResponseEntity.ok(users);
  }

  /**
   * Deletes a user from the system by their ID.
   *
   * @param id The ID of the user to delete
   * @return ResponseEntity with a success message upon deletion
   */
  @DeleteMapping("/user/{id}")
  public ResponseEntity<String> deleteSellerById(@PathVariable int id) {
    userServiceImpl.deleteById(id);
    return ResponseEntity.ok(UserConstMessages.USER_DELETED_SUCCESSFULLY);
  }
}