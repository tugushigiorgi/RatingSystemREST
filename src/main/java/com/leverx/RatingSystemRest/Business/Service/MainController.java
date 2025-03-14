package com.leverx.RatingSystemRest.Business.Service;


import com.leverx.RatingSystemRest.Presentation.Dto.SellerProfileDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserInfoDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class MainController {
    private UserService userService;


    @GetMapping("/topselelrs")
    public ResponseEntity<List<UserInfoDto>> topRatedSelelrs() {

        return userService.getTopRatedSellers();

    }


    @GetMapping("/SellerProfile/{sellerId}")
    public ResponseEntity<SellerProfileDto> getSellerProfileData(@PathVariable int sellerId) {

        return userService.GetSellerProfileById(sellerId);

    }


}
