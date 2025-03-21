package com.leverx.RatingSystemRest.Presentation.Controllers;


import com.leverx.RatingSystemRest.Business.CommentService;
import com.leverx.RatingSystemRest.Business.EmailService;
import com.leverx.RatingSystemRest.Business.GameObjectService;
import com.leverx.RatingSystemRest.Business.UserService;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.SellerProfileDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.UserInfoDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.addCommentDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/main")
public class MainController {
    private UserService userService;
    private EmailService emailService;
    private GameObjectService gameObjectService;
    private CommentService commentService;
    @GetMapping("/topselers")
    public ResponseEntity<List<UserInfoDto>> topRatedSelelrs() {

        return userService.getTopRatedSellers();

    }


    @GetMapping("/SellerProfile/{sellerId}")
    public ResponseEntity<SellerProfileDto> getSellerProfileData(@PathVariable int sellerId) {

        return userService.GetSellerProfileById(sellerId);

    }

    @GetMapping("/search")
    public ResponseEntity<List<GameObjectDto>> searchGames(
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int sellerRating) {

        return gameObjectService.searchGameObjects(sellerRating, title);
    }

    @PostMapping("/addcomment")
    public ResponseEntity<String> add( @Valid @RequestBody addCommentDto dto ){

        return commentService.add(dto);
    }

    @PutMapping("/comment")
    public ResponseEntity<String> updateComment( @Valid @RequestBody CommentUpdateDto dto ){
        return  commentService.update(dto);
    }


}
