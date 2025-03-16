package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Presentation.Dto.ChangePasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.RegisterUserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<String> RegisterUser(@ModelAttribute RegisterUserDto dto,    @RequestParam("photo") MultipartFile photo){

        return userService.registerUser(dto,photo);


    }


    @PostMapping("/recovercode/{email}")
    public ResponseEntity<String> PasswordRecoverCodeSend(@PathVariable String email){

        return userService.SendRecoverCode(email);

    }


    @PostMapping("/updatepassword")
    public ResponseEntity<String> ChangePassword(@RequestBody RecoverPasswordDto dto){

            return  userService.updatePassword(dto);

    }





    @PostMapping("/verify/{code}")
    public ResponseEntity<String> verifyEmailCode(@PathVariable String code){

        return userService.ActivateAccount(code);

    }




















}
