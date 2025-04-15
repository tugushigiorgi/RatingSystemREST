package com.leverx.RatingSystemRest.Business.Interfaces;

import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.UpdateGameObject;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.addGameObjectDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GameObjectService {


     ResponseEntity<String> add(addGameObjectDto dto, MultipartFile photo, int userId);
        ResponseEntity<String> remove(int gameObjectId, int userId) ;
    ResponseEntity<String> update(UpdateGameObject dto, MultipartFile photo, int userId);
    ResponseEntity<List<GameObjectDto>> getGameObjectsBySellerId(int sellerId) throws Exception;
    ResponseEntity<List<GameObjectDto>> searchGameObjects(int sellerRating, String title);




}
