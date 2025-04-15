package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Interfaces.UserService;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.isAdminDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService ;

    @PutMapping("/password")
    public ResponseEntity<String> ChangePassword(@Valid @RequestBody ChangePasswordDto dto, Authentication authentication) {

        var currentUserId = userService .retriaveLogedUserId(authentication);
        if (currentUserId != 0) {
            return userService.changePassword(currentUserId, dto);
        }
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/verifyrole")
    public ResponseEntity<isAdminDto> isAdmin(Authentication authentication) {
        return userService.checkifAdmin(authentication);
    }


}
