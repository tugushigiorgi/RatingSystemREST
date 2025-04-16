package com.leverx.RatingSystemRest.Business.impl;

import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.PERMISSION_DENIED_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.PICTURE_CANNOT_BE_SAVED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.GAME_ADDED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.GAME_NOT_FOUND;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.OBJECT_UPDATED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.SOMETHING_WENT_WRONG;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.SUCCESSFULLY_DELETED_WITH_ID;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.SELLER_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.leverx.RatingSystemRest.Business.Interfaces.GameObjectService;
import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObjectPicture;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectPictureRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserPhotoRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.UpdateGameObject;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.addGameObjectDto;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementation of the  GameObjectService interface.
 * This service provides operations for managing game objects, such as adding, updating,
 * deleting, and retrieving game listings associated with a seller.
 * </p>
 */
@RequiredArgsConstructor
@Service
public class GameObjectServiceImp implements GameObjectService {

  private final UserPhotoRepository userPhotoRepository;
  private final GameObjectRepository gameObjectRepository;
  private final UserRepository userRepository;
  private final GameObjectPictureRepository gameObjectPictureRepository;

  @Value("${file.upload-dir}")
  private String uploadDir;

  /**
   * Adds a new game object with an associated picture.
   *
   * @param dto    the game object data transfer object
   * @param photo  the image file associated with the game
   * @param userId the ID of the seller adding the game
   * @return HTTP response indicating success or failure
   */
  @Override
  public ResponseEntity<String> add(addGameObjectDto dto, MultipartFile photo, int userId) {
    var currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND));
    var savedPicture = saveNewPictureOnLocalFolder(userId, photo);
    if (savedPicture == null) {
      return new ResponseEntity<>(PICTURE_CANNOT_BE_SAVED, INTERNAL_SERVER_ERROR);
    }
    var gameObject = GameObject.builder()
        .price(dto.getPrice())
        .title(dto.getTitle())
        .text(dto.getText())
        .user(currentUser)
        .createdAt(LocalDateTime.now())
        .picture(savedPicture)
        .build();
    savedPicture.setGameObject(gameObject);
    gameObjectRepository.save(gameObject);

    return new ResponseEntity<>(GAME_ADDED, OK);
  }

  /**
   * Removes a game object if the user is authorized.
   *
   * @param gameObjectId the ID of the game object to be removed
   * @param userId       the ID of the requesting user
   * @return HTTP response indicating success or failure
   */
  @Override
  @Transactional
  public ResponseEntity<String> remove(int gameObjectId, int userId) {
    var getcurrentUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND));

    var currentObject = gameObjectRepository.findById(gameObjectId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, GAME_NOT_FOUND));

    if (!Objects.equals(currentObject.getUser().getId(), getcurrentUser.getId())) {
      return new ResponseEntity<>(PERMISSION_DENIED_MESSAGE, BAD_REQUEST);
    }

    try {
      gameObjectRepository.delete(currentObject);
      var path = Paths.get(uploadDir + File.separator + currentObject.getPicture().getUrl());
      Files.delete(path);
      return new ResponseEntity<>(SUCCESSFULLY_DELETED_WITH_ID + gameObjectId, OK);
    } catch (Exception e) {
      return new ResponseEntity<>(SOMETHING_WENT_WRONG, INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Updates a game object and optionally its associated image.
   *
   * @param dto    the update request
   * @param photo  a new image file, if the image is being updated
   * @param userId the ID of the user performing the update
   * @return HTTP response indicating success or failure
   */
  @Override
  public ResponseEntity<String> update(UpdateGameObject dto, MultipartFile photo, int userId) {
    var currentUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND));

    var currentObject = gameObjectRepository.findById(dto.getId())
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, GAME_NOT_FOUND));

    if (!Objects.equals(currentObject.getUser().getId(), currentUser.getId())) {
      return new ResponseEntity<>(PERMISSION_DENIED_MESSAGE, BAD_REQUEST);
    }

    if (dto.getPrice() != 0 && dto.price != currentObject.getPrice()) {
      currentObject.setPrice(dto.getPrice());
    }
    if (dto.getTitle() != null && !currentObject.getTitle().equals(dto.getTitle())) {
      currentObject.setTitle(dto.getTitle());
    }
    if (dto.getText() != null && !currentObject.getText().equals(dto.getText())) {
      currentObject.setText(dto.getText());
    }

    if (photo != null) {
      var updatePicture = updatePictureOnLocalFolder(currentObject.getPicture().getUrl(), userId, photo);
      if (updatePicture != null) {
        currentObject.setPicture(updatePicture);
        currentObject.setUpdatedAt(LocalDateTime.now());
        updatePicture.setGameObject(currentObject);
        gameObjectPictureRepository.delete(currentObject.getPicture());
        gameObjectRepository.save(currentObject);
        gameObjectPictureRepository.save(updatePicture);
        return new ResponseEntity<>(OBJECT_UPDATED, OK);
      }
    }

    currentObject.setUpdatedAt(LocalDateTime.now());
    gameObjectRepository.save(currentObject);
    return new ResponseEntity<>(OBJECT_UPDATED, OK);
  }

  /**
   * Retrieves all game objects for a specific seller.
   *
   * @param sellerId the ID of the seller
   * @return list of game object DTOs or 204 if empty
   * @throws Exception if the seller does not exist
   */
  @Override
  public ResponseEntity<List<GameObjectDto>> getGameObjectsBySellerId(int sellerId) throws Exception {
    var getUser = userRepository.findById(sellerId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND));

    var getGames = gameObjectRepository.getGameObjectsBySellerId(sellerId);

    if (CollectionUtils.isEmpty(getGames)) {
      return new ResponseEntity<>(NO_CONTENT);
    }

    var toDto = getGames.stream().map(GameObjectDto::toDto).toList();
    return ResponseEntity.ok(toDto);
  }

  /**
   * Searches game objects by title and/or seller rating.
   *
   * @param sellerRating the minimum rating of the seller
   * @param title        the title or partial title to filter by
   * @return list of filtered game object DTOs or 204 if none match
   */
  @Override
  public ResponseEntity<List<GameObjectDto>> searchGameObjects(int sellerRating, String title) {
    List<GameObject> data;

    if (title == null || title.isBlank()) {
      data = gameObjectRepository.filterBySellerRating(sellerRating);
    } else {
      data = gameObjectRepository.filterByTitleAndRating(title, sellerRating);
    }

    if (CollectionUtils.isEmpty(data)) {
      return new ResponseEntity<>(NO_CONTENT);
    }

    var dtoList = data.stream().map(GameObjectDto::toDto).toList();
    return ResponseEntity.ok(dtoList);
  }

  /**
   * Saves a new picture to the local file system under a user-specific directory.
   *
   * @param userId  the ID of the user
   * @param picture the image file to save
   * @return the saved {@link GameObjectPicture} entity, or null if saving fails
   */
  private GameObjectPicture saveNewPictureOnLocalFolder(int userId, MultipartFile picture) {
    var userFolderPath = uploadDir + File.separator + userId + File.separator + "GAMES";
    var userFolder = new File(userFolderPath);
    if (!userFolder.exists()) {
      userFolder.mkdirs();
    }

    var uuid = UUID.randomUUID();
    var modifiedFileName = uuid + picture.getOriginalFilename();
    var publicUrl = userId + File.separator + "GAMES" + File.separator + modifiedFileName;
    var filePath = userFolderPath + File.separator + modifiedFileName;

    try {
      var savedFile = new File(filePath);
      picture.transferTo(savedFile);
      return GameObjectPicture.builder()
          .url(publicUrl)
          .size(picture.getSize())
          .extension(picture.getContentType())
          .photoName(modifiedFileName)
          .build();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Updates an existing picture file on the local file system.
   *
   * @param currentFileUrl the path of the current image file
   * @param userId         the user ID to determine storage directory
   * @param file           the new image file to replace the existing one
   * @return the updated {@link GameObjectPicture} or null if update fails
   */
  private GameObjectPicture updatePictureOnLocalFolder(String currentFileUrl, int userId, MultipartFile file) {
    try {
      var path = Paths.get(uploadDir + File.separator + currentFileUrl);
      Files.delete(path);
      return saveNewPictureOnLocalFolder(userId, file);
    } catch (IOException e) {
      return null;
    }
  }
}
