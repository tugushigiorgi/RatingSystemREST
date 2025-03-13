package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Presentation.Dto.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UpdateGameObject;
import com.leverx.RatingSystemRest.Presentation.Dto.addGameObjectDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/game")
public class GameController {

    private GameObjectService gameObjectService;


    @PostMapping
    public ResponseEntity<String> addGame(
            @ModelAttribute addGameObjectDto dto,
            @RequestParam("photo") MultipartFile photo) {
  return gameObjectService.add(dto,photo,2);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteGame(@PathVariable int id) {


        //TODO CURRENT LOGGED USER ID PASS
        return gameObjectService.remove(id,2);

    }


    @PutMapping("detailed")
    public ResponseEntity<String> updateGameWithPhoto(  @ModelAttribute UpdateGameObject dto, @RequestParam("photo") MultipartFile photo) {


        //TODO CURRENT LOGGED USER ID PASS
        return gameObjectService.update(dto,photo,2);

    }

    @PutMapping()
    public ResponseEntity<String> updateGameWithPhoto(  @ModelAttribute UpdateGameObject dto) {


        //TODO CURRENT LOGGED USER ID PASS
        return gameObjectService.update(dto,null,2);

    }




    @GetMapping("/{sellerId}")
    public List<GameObjectDto> getSellerGames(@PathVariable int sellerId) throws Exception {

        return gameObjectService.getGameObjectsBySellerId(sellerId);

    }




}
