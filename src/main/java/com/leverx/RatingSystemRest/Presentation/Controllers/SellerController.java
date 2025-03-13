package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Presentation.Dto.GameObjectDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/seller")
public class SellerController {
    private GameObjectService gameObjectService;






    @GetMapping("/games")
    public List<GameObjectDto> MyGames() throws Exception {
       //TODO CURRENTLY LOGGED USER ID
     return   gameObjectService.getGameObjectsBySellerId(2);

   }








}
