package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.UpdateGameObject;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.addGameObjectDto;
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

    @PostMapping
    public ResponseEntity<String> addGame(
            @ModelAttribute addGameObjectDto dto,
            @RequestParam("photo") MultipartFile photo, Authentication authentication) {



        if(authentication.getName()!=null) {
            var user = userRepository.findByEmail(authentication.getName());

            return gameObjectService.add(dto,photo,user.get().getId());
        }

    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);



    }


    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteGame(@PathVariable int id,Authentication authentication) {

        if(authentication.getName()!=null) {
            var user = userRepository.findByEmail(authentication.getName());


        return gameObjectService.remove(id,user.get().getId());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @PutMapping("detailed")
    public ResponseEntity<String> updateGameWithPhoto(  @ModelAttribute UpdateGameObject dto, @RequestParam("photo") MultipartFile photo,Authentication authentication) {


        //TODO CURRENT LOGGED USER ID PASS

        if(authentication.getName()!=null) {
            var user = userRepository.findByEmail(authentication.getName());
            return gameObjectService.update(dto,photo,user.get().getId());

        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);



    }

    @PutMapping()
    public ResponseEntity<String> updateGameWithPhoto(  @ModelAttribute UpdateGameObject dto,Authentication authentication) {
        if(authentication.getName()!=null) {
            var user = userRepository.findByEmail(authentication.getName());
            return gameObjectService.update(dto,null,user.get().getId());

        }

      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);


    }




    @GetMapping("/{sellerId}")
    public List<GameObjectDto> getSellerGames(@PathVariable int sellerId) throws Exception {




        return gameObjectService.getGameObjectsBySellerId(sellerId);

    }




}
