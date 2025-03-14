package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Presentation.Dto.ChangePasswordDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PutMapping("/password")
    public ResponseEntity<String> ChangePassword(@RequestBody  ChangePasswordDto dto){

        //TODO Currently logged user
        return userService.ChangePassword(2,dto);
    }


}
