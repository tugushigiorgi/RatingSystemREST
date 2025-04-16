package com.leverx.RatingSystemRest.Presentation.Controllers;


import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Business.Interfaces.CommentService;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.AdminNotApprovedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.DetailedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@Slf4j
public class AdminController {


    private final UserServiceImpl userServiceImpl;

    private final CommentService commentService;

    //TODO  uppercase -> lower case methods name
    //GOOGle CHECKSTYLE
    @GetMapping("/users/requests")
    public ResponseEntity<List<AdminNotApprovedUserDto>> RegistrationRequestsList() {
        return userServiceImpl.getSellersRegistrationRequests();
    }


    @PostMapping("/user/approve/{id}")
    public ResponseEntity<String> approveRegistrationRequest(@PathVariable int id) {


        return userServiceImpl.acceptSellerRegistrationRequest(id);
    }

    @GetMapping("/comments/requests")
    public List<UserReviewsDto> ReviewsRequestsList() throws Exception {

        return commentService.getAllNotApprovedReviews();
    }


    @PostMapping("/comments/approve/{id}")
    public ResponseEntity<String> approveReviewRequest(@PathVariable int id) {
        System.out.println("Approving Review Request");

        return commentService.approveUserReview(id);
    }

    @DeleteMapping("/comments/decline/{id}")
    public ResponseEntity<String> declineReviewRequest(@PathVariable int id) {


        return commentService.declineUserReview(id);
    }


    @GetMapping("/user/registeredusers")
    public ResponseEntity<List<DetailedUserDto>> getDetailedRegisteredUserList() {

        return userServiceImpl.detailedRegisteredUsers();

    }

    @GetMapping("/user/registeredusers/{username}")
    public ResponseEntity<List<DetailedUserDto>> getDetailedRegisteredUserListByUsername(@PathVariable String username) {

        return userServiceImpl.getDetailedRegisteredUsersByUsername(username);

    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> DeleteSellerById(@PathVariable int id) {
        return userServiceImpl.deleteById(id);
    }


}
