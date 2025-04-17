package com.leverx.RatingSystemRest.Presentation.Controllers;

import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.USER_DELETED_SUCCESSFULLY;
import static com.leverx.RatingSystemRest.Presentation.ConstMessages.AdminConstMessages.ACCEPTED_SELLER_REGISTRATION;
import static com.leverx.RatingSystemRest.Presentation.ConstMessages.AdminConstMessages.FAILED_APPROVE_SELLER_REGISTRATION;
import static com.leverx.RatingSystemRest.Presentation.ConstMessages.CommentConstMessages.COMMENT_APPROVED;
import static com.leverx.RatingSystemRest.Presentation.ConstMessages.CommentConstMessages.COMMENT_DELETED_MESSAGE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.leverx.RatingSystemRest.Business.Interfaces.CommentService;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.AdminNotApprovedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.DetailedUserDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller that handles all admin-related operations such as user and comment management.
 */
@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {

  private final UserServiceImpl userServiceImpl;
  private final CommentService commentService;

  /**
   * Fetches a list of sellers' registration requests awaiting approval.
   *
   * @return a {@link ResponseEntity} containing a list of sellers' registration requests
   *         and an appropriate HTTP status (204 if no content, 200 if data exists)
   */
  @GetMapping("/users/requests")
  public ResponseEntity<List<AdminNotApprovedUserDto>> registrationRequestsList() {
    List<AdminNotApprovedUserDto> registrationRequests = userServiceImpl.getSellersRegistrationRequests();
    if (registrationRequests.isEmpty()) {
      return new ResponseEntity<>(NO_CONTENT);
    }
    return new ResponseEntity<>(registrationRequests, OK);
  }

  /**
   * Approves a seller's registration request based on the seller's ID.
   *
   * @param id the ID of the seller whose registration request is to be approved
   * @return a {@link ResponseEntity} with a message and an appropriate HTTP status
   *         (200 if approved, 400 if failed)
   */
  @PostMapping("/user/approve/{id}")
  public ResponseEntity<String> acceptSellerRegistrationRequest(@PathVariable int id) {
    try {
      boolean isApproved = userServiceImpl.acceptSellerRegistrationRequest(id);

      if (isApproved) {
        return new ResponseEntity<>(ACCEPTED_SELLER_REGISTRATION, OK);
      } else {
        return new ResponseEntity<>(FAILED_APPROVE_SELLER_REGISTRATION, BAD_REQUEST);
      }

    } catch (ResponseStatusException ex) {
      return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
  }

  /**
   * Fetches a list of comments that are awaiting approval.
   *
   * @return a {@link ResponseEntity} containing a list of comments pending approval
   *         and an appropriate HTTP status (204 if no content, 200 if data exists)
   * @throws Exception if there is an error during the fetching process
   */
  @GetMapping("/comments/requests")
  public ResponseEntity<List<UserReviewsDto>> reviewsRequestsList() throws Exception {
    try {
      List<UserReviewsDto> reviews = commentService.getAllNotApprovedReviews();

      if (reviews.isEmpty()) {
        return new ResponseEntity<>(NO_CONTENT);
      }
      return new ResponseEntity<>(reviews, OK);

    } catch (Exception e) {
      return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Approves a specific review based on its ID.
   *
   * @param id the ID of the comment to be approved
   * @return a {@link ResponseEntity} containing a success message and an HTTP status (200)
   */
  @PostMapping("/comments/approve/{id}")
  public ResponseEntity<String> approveReviewRequest(@PathVariable int id) {
    try {
      commentService.approveUserReview(id);
      return new ResponseEntity<>(COMMENT_APPROVED, OK);
    } catch (ResponseStatusException ex) {
      return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
  }

  /**
   * Declines a specific review based on its ID.
   *
   * @param id the ID of the comment to be declined
   * @return a {@link ResponseEntity} containing a message about the deletion and an HTTP status (200)
   */
  @DeleteMapping("/comments/decline/{id}")
  public ResponseEntity<String> declineReviewRequest(@PathVariable int id) {
    try {
      commentService.declineUserReview(id);
      return new ResponseEntity<>(COMMENT_DELETED_MESSAGE, OK);
    } catch (ResponseStatusException ex) {
      return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
  }

  /**
   * Fetches a list of all detailed registered users.
   *
   * @return a {@link ResponseEntity} containing a list of registered users
   *         and an appropriate HTTP status (204 if no content, 200 if data exists)
   */
  @GetMapping("/user/registeredusers")
  public ResponseEntity<List<DetailedUserDto>> getDetailedRegisteredUserList() {
    List<DetailedUserDto> userList = userServiceImpl.detailedRegisteredUsers();
    if (userList.isEmpty()) {
      return new ResponseEntity<>(NO_CONTENT);
    }
    return new ResponseEntity<>(userList, OK);
  }

  /**
   * Fetches a list of registered users based on a given username.
   *
   * @param username the username to search for in the registered users list
   * @return a {@link ResponseEntity} containing a list of users that match the username
   *         and an appropriate HTTP status (204 if no content, 200 if data exists)
   */
  @GetMapping("/user/registeredusers/{username}")
  public ResponseEntity<List<DetailedUserDto>> getDetailedRegisteredUserListByUsername(@PathVariable String username) {
    List<DetailedUserDto> users = userServiceImpl.getDetailedRegisteredUsersByUsername(username);
    if (users.isEmpty()) {
      return new ResponseEntity<>(NO_CONTENT);
    }
    return new ResponseEntity<>(users, OK);
  }

  /**
   * Deletes a seller by their ID.
   *
   * @param id the ID of the seller to be deleted
   * @return a {@link ResponseEntity} containing a success message and an HTTP status (200)
   */
  @DeleteMapping("/user/{id}")
  public ResponseEntity<String> deleteSellerById(@PathVariable int id) {
    try {
      userServiceImpl.deleteById(id);
      return new ResponseEntity<>(USER_DELETED_SUCCESSFULLY, OK);

    } catch (ResponseStatusException ex) {
      return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
  }
}
