package com.leverx.RatingSystemRest.Presentation.Controllers;


import com.leverx.RatingSystemRest.Business.Interfaces.CommentService;
import com.leverx.RatingSystemRest.Business.impl.EmailServiceImp;
import com.leverx.RatingSystemRest.Business.impl.GameObjectServiceImp;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.CommentUpdateDto;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.AddCommentDto;
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

@RestController
@AllArgsConstructor
@RequestMapping("/api/main")
public class MainController {
    private UserServiceImpl userServiceImpl;
    private EmailServiceImp emailService;
    private GameObjectServiceImp gameObjectService;
    private CommentService commentService;
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
    public ResponseEntity<String> add( @Valid @RequestBody AddCommentDto dto ){

        return commentService.add(dto);
    }

    @PutMapping("/comment")
    public ResponseEntity<String> updateComment( @Valid @RequestBody CommentUpdateDto dto ){
        return  commentService.update(dto);
    }


}
