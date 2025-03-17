package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.isAdminDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PutMapping("/password")
    public ResponseEntity<String> ChangePassword(@RequestBody ChangePasswordDto dto, Authentication authentication) {

        var currentUserId = userService.RetriaveLogedUserId(authentication);
        if (currentUserId != 0) {
            return userService.ChangePassword(currentUserId, dto);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/verifyrole")
    public ResponseEntity<isAdminDto> isAdmin(Authentication authentication) {
        return userService.CheckifAdmin(authentication);
    }


}
