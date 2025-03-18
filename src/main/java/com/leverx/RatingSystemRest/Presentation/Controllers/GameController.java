package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Business.Service.UserService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.UpdateGameObject;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.addGameObjectDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/game")
public class GameController {

    private GameObjectService gameObjectService;
    private UserRepository userRepository;
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> addGame(
           @Valid @ModelAttribute addGameObjectDto dto,
            @RequestParam("photo") MultipartFile photo, Authentication authentication) {
        var currentUserId = userService.RetriaveLogedUserId(authentication);
        if (currentUserId != 0) {
            return gameObjectService.add(dto, photo, currentUserId);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }


    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteGame(@PathVariable int id, Authentication authentication) {

        var currentUserId = userService.RetriaveLogedUserId(authentication);
        if (currentUserId != 0) {

            return gameObjectService.remove(id, currentUserId);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @PutMapping("detailed")
    public ResponseEntity<String> updateGameWithPhoto(@Valid @ModelAttribute UpdateGameObject dto, @RequestParam("photo") MultipartFile photo, Authentication authentication) {
        var currentUserId = userService.RetriaveLogedUserId(authentication);
        if (currentUserId != 0) {
            return gameObjectService.update(dto, photo, currentUserId);

        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


    }

    @PutMapping()
    public ResponseEntity<String> updateGameWithPhoto( @Valid @ModelAttribute UpdateGameObject dto, Authentication authentication) {
        var currentUserId = userService.RetriaveLogedUserId(authentication);
        if (currentUserId != 0) {
            return gameObjectService.update(dto, null, currentUserId);

        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


    }


}
