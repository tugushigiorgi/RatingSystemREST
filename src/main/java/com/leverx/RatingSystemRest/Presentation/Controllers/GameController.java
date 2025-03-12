package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Presentation.Dto.GameObjectDto;
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


        if (photo == null || photo.isEmpty()) {
            return new ResponseEntity<>("Photo is missing", HttpStatus.BAD_REQUEST);
        }


        return gameObjectService.add(dto,photo,2);
    }











}
