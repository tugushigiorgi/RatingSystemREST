package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.CommentService;
import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.AdminNotApprovedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.DetailedUserDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private UserService userService;

    private CommentService commentService;

    @GetMapping("/users/requests")
    public List<AdminNotApprovedUserDto> RegistrationRequestsList() {


        return userService.getSellersRegistrationRequests();


    }


    @PostMapping("/user/approve/{id}")
    public ResponseEntity<String> approveRegistrationRequest(@PathVariable int id) {


        return userService.AcceptSellerRegistrationRequest(id);
    }

    @GetMapping("/comments/requests")
    public List<UserReviewsDto> ReviewsRequestsList() throws Exception {

        return commentService.getAllNotApprovedReviews();
    }


    @PostMapping("/comments/approve/{id}")
    public ResponseEntity<String> approveReviewRequest(@PathVariable int id) {
        System.out.println("Approving Review Request");

        return commentService.ApproveUserReview(id);
    }

    @DeleteMapping("/comments/decline/{id}")
    public ResponseEntity<String> declineReviewRequest(@PathVariable int id) {


        return commentService.DeclineUserReview(id);
    }


    @GetMapping("/user/registeredusers")
    public List<DetailedUserDto> getDetailedRegisteredUserList() {

        return userService.DetailedRegisteredUsers();

    }

    @GetMapping("/user/registeredusers/{username}")
    public List<DetailedUserDto> getDetailedRegisteredUserListByUsername(  @PathVariable String username) {

        return userService.GetDetailedRegisteredUsersByUsername(username);

    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> DeleteSellerById( @PathVariable int  id) {
     return   userService.DeleteById(id);
    }



}
