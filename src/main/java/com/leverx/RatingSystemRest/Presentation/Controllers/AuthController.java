package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.RecoverPasswordDto;
import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.jwtDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.Logindto;
import com.leverx.RatingSystemRest.Presentation.Dto.UserDtos.RegisterUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {


    private final UserServiceImpl userServiceImpl;

    private AuthenticationManager authenticationManager;
    @PostMapping("/register")
    public ResponseEntity<String> RegisterUser(@ModelAttribute RegisterUserDto dto,    @RequestParam("photo") MultipartFile photo){

        return userServiceImpl.registerUser(dto,photo);


    }
    @PostMapping("/login")
    public ResponseEntity<jwtDto> Login(@Valid @RequestBody Logindto loginDTO ) throws InternalAuthenticationServiceException {

        return  userServiceImpl.login(authenticationManager, loginDTO);

    }
    @PostMapping("/recovercode/{email}")
    public ResponseEntity<String> PasswordRecoverCodeSend(@PathVariable String email){

        return userServiceImpl.sendRecoverCode(email);

    }

    @PostMapping("/updatepassword")
    public ResponseEntity<String> ChangePassword( @Valid @RequestBody RecoverPasswordDto dto){

            return  userServiceImpl.updatePassword(dto);

    }
    @PostMapping("/verify/{code}")
    public ResponseEntity<String> verifyEmailCode(@PathVariable String code){

        return userServiceImpl.activateAccount(code);

    }
    @PostMapping("/logout")
    public  ResponseEntity<String> Logout( HttpServletRequest request){

        try{
            request.getSession().invalidate();
            SecurityContextHolder.clearContext();

            return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e);
             return new ResponseEntity<>("Logged out failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
