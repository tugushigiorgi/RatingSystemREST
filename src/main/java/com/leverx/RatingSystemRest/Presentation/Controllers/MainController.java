package com.leverx.RatingSystemRest.Presentation.Controllers;



import com.leverx.RatingSystemRest.Business.impl.EmailServiceImp;
import com.leverx.RatingSystemRest.Business.impl.GameObjectServiceImp;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Business.Interfaces.commentService;
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
    private UserServiceImpl userServiceImpl;
    private EmailServiceImp emailService;
    private GameObjectServiceImp gameObjectService;
    private commentService commentService;
    @GetMapping("/topselers")
    public ResponseEntity<List<UserInfoDto>> topRatedSelelrs() {

        return userServiceImpl.getTopRatedSellers();

    }


    @GetMapping("/SellerProfile/{sellerId}")
    public ResponseEntity<SellerProfileDto> getSellerProfileData(@PathVariable int sellerId) {

        return userServiceImpl.getSellerProfileById(sellerId);

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
