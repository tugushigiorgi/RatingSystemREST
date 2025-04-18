package com.leverx.RatingSystemRest.Business.Interfaces;

import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.UpdateGameObject;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.addGameObjectDto;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contract for GameObjectService which provides methods
 * for creating, updating, removing, and retrieving game objects.
 */
public interface GameObjectService {

  /**
   * Adds a new GameObject.
   *
   * @param dto    the data transfer object containing GameObject data
   * @param photo  the GameObject picture uploaded from local storage
   * @param userId the ID of the owner of the GameObject

   */
  void add(addGameObjectDto dto, MultipartFile photo, int userId);

  /**
   * Removes a GameObject.
   *
   * @param gameObjectId the ID of the GameObject to be removed
   * @param userId       the ID of the owner of the GameObject

   */
  void remove(int gameObjectId, int userId);

  /**
   * Updates an existing GameObject.
   *
   * @param dto    the data transfer object containing updated GameObject data
   * @param photo  the new GameObject picture uploaded from local storage
   * @param userId the ID of the owner of the GameObject

   */
  void update(UpdateGameObject dto, MultipartFile photo, int userId);

  /**
   * Retrieves GameObjects owned by the specified seller.
   *
   * @param sellerId the ID of the seller
   * @return a response containing the list of GameObjects
   * @throws Exception if an error occurs while retrieving the data
   */
  ResponseEntity<List<GameObjectDto>> getGameObjectsBySellerId(int sellerId) throws Exception;

  /**
   * Searches for GameObjects based on seller rating and title.
   *
   * @param sellerRating the minimum average rating of the seller
   * @param title        the title of the GameObject to search for
   * @return a response containing the list of matching GameObjects
   */
  ResponseEntity<List<GameObjectDto>> searchGameObjects(int sellerRating, String title);
}
