package com.leverx.RatingSystemRest.Presentation.Controllers;

import com.leverx.RatingSystemRest.Business.Service.GameObjectService;
import com.leverx.RatingSystemRest.Presentation.Dto.GameObjectDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/seller")
public class SellerController {
    private GameObjectService gameObjectService;


    @GetMapping("games/{sellerId}")
    public List<GameObjectDto> getSellerGames(@PathVariable int sellerId) throws Exception {

        return gameObjectService.getGameObjectsBySellerId(sellerId);



    }








}
