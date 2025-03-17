package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    @PutMapping("/password")
    public ResponseEntity<String> ChangePassword(@RequestBody  ChangePasswordDto dto, Authentication authentication) {

        if(authentication.getName()!=null) {
            var user = userRepository.findByEmail(authentication.getName());


            return userService.ChangePassword(user.get().getId(),dto);
        }
       return  new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }









}
