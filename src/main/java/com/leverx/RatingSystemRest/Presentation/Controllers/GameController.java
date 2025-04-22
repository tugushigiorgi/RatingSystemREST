package com.leverx.RatingSystemRest.Presentation.Controllers;

import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.GAME_ADDED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.OBJECT_UPDATED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.SUCCESSFULLY_DELETED_WITH_ID;

import com.leverx.RatingSystemRest.Business.Interfaces.GameObjectService;
import com.leverx.RatingSystemRest.Business.impl.UserServiceImpl;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.UpdateGameObject;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.addGameObjectDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller that handles game object operations.
 * Provides endpoints for adding, updating, and deleting game objects by authenticated users.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/game")
public class GameController {

  private final GameObjectService gameObjectService;
  private final UserServiceImpl userServiceImpl;

  /**
   * Adds a new game object to the system with details and an image.
   *
   * @param dto The DTO containing game object information
   * @param photo The image file for the game object
   * @param authentication The authentication object containing user credentials
   * @return ResponseEntity with a success message upon successful addition
   */
  @PostMapping
  public ResponseEntity<String> addGame(
      @Valid @ModelAttribute addGameObjectDto dto,
      @RequestParam("photo") MultipartFile photo,
      Authentication authentication) {

    var currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    gameObjectService.add(dto, photo, currentUserId);
    return ResponseEntity.ok(GAME_ADDED);
  }

  /**
   * Deletes a game object from the system by its ID.
   * Only allows deletion if the authenticated user is the owner of the game object.
   *
   * @param id The ID of the game object to delete
   * @param authentication The authentication object containing user credentials
   * @return ResponseEntity with a success message including the deleted game ID
   */
  @DeleteMapping("{id}")
  public ResponseEntity<String> deleteGame(@PathVariable int id, Authentication authentication) {
    var currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    gameObjectService.remove(id, currentUserId);
    return ResponseEntity.ok(SUCCESSFULLY_DELETED_WITH_ID + id);
  }

  /**
   * Updates a game object with new details and replaces its image.
   * Only allows updates if the authenticated user is the owner of the game object.
   *
   * @param dto The DTO containing updated game object information
   * @param photo The new image file for the game object
   * @param authentication The authentication object containing user credentials
   * @return ResponseEntity with a success message upon successful update
   */
  @PutMapping("detailed")
  public ResponseEntity<String> updateGameWithPhoto(
      @Valid @ModelAttribute UpdateGameObject dto,
      @RequestParam("photo") MultipartFile photo,
      Authentication authentication) {

    var currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    gameObjectService.update(dto, photo, currentUserId);
    return ResponseEntity.ok(OBJECT_UPDATED);
  }

  /**
   * Updates a game object with new details without changing its image.
   * Only allows updates if the authenticated user is the owner of the game object.
   *
   * @param dto The DTO containing updated game object information
   * @param authentication The authentication object containing user credentials
   * @return ResponseEntity with a success message upon successful update
   */
  @PutMapping
  public ResponseEntity<String> updateGameWithoutPhoto(
      @Valid @ModelAttribute UpdateGameObject dto,
      Authentication authentication) {

    int currentUserId = userServiceImpl.retriaveLogedUserId(authentication);
    gameObjectService.update(dto, null, currentUserId);
    return ResponseEntity.ok(OBJECT_UPDATED);
  }
}