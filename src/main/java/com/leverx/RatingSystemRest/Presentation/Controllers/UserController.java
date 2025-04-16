package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Interfaces.UserService;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.IsAdminDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
  public ResponseEntity<String> ChangePassword(@Valid @RequestBody ChangePasswordDto dto, Authentication authentication) {

    var currentUserId = userService.retriaveLogedUserId(authentication);
    if (currentUserId != 0) {
      return userService.changePassword(currentUserId, dto);
    }
    return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
  }

  @GetMapping("/verifyrole")
  public ResponseEntity<IsAdminDto> isAdmin(Authentication authentication) {
    return userService.checkifAdmin(authentication);
  }


}
