package com.leverx.RatingSystemRest.Business.impl;

import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.PERMISSION_DENIED_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.PICTURE_CANNOT_BE_SAVED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.GAME_NOT_FOUND;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.SOMETHING_WENT_WRONG;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.SELLER_NOT_FOUND;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
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
   */
  @Override
  public void add(addGameObjectDto dto, MultipartFile photo, int userId) {
    log.debug("Attempting to add new game object for userId: {}", userId);

    var currentUser = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("User with id {} not found", userId);
          return new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND);
        });

    var savedPicture = saveNewPictureOnLocalFolder(userId, photo);
    if (savedPicture == null) {
      log.error("Failed to save picture for game object by userId: {}", userId);
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, PICTURE_CANNOT_BE_SAVED);
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
    log.info("Game object added successfully for userId: {}", userId);
  }


  /**
   * Removes a game object if the user is authorized.
   *
   * @param gameObjectId the ID of the game object to be removed
   * @param userId       the ID of the requesting user
   */
  @Override
  @Transactional
  public void remove(int gameObjectId, int userId) {
    log.debug("Attempting to remove game object with id {} by userId {}", gameObjectId, userId);

    var getCurrentUser = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("User with Id {} not found", userId);
          return new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND);
        });

    var currentObject = gameObjectRepository.findById(gameObjectId)
        .orElseThrow(() -> {
          log.error("Game object with id {} not found", gameObjectId);
          return new ResponseStatusException(BAD_REQUEST, GAME_NOT_FOUND);
        });

    if (!Objects.equals(currentObject.getUser().getId(), getCurrentUser.getId())) {
      log.warn("User with id {} is not authorized to delete game with id {}", userId, gameObjectId);
      throw new ResponseStatusException(BAD_REQUEST, PERMISSION_DENIED_MESSAGE);
    }

    try {
      gameObjectRepository.delete(currentObject);

      var path = Paths.get(uploadDir + File.separator + currentObject.getPicture().getUrl());
      Files.deleteIfExists(path);
      log.info("Game object with id {} removed successfully by userId {}", gameObjectId, userId);

    } catch (IOException e) {
      log.error("Error deleting the file for game with id {}by userId {}", gameObjectId, userId, e);
      throw new ResponseStatusException(INTERNAL_SERVER_ERROR, SOMETHING_WENT_WRONG);
    }
  }


  /**
   * Updates a game object and optionally its associated image.
   *
   * @param dto    the update request
   * @param photo  a new image file, if the image is being updated
   * @param userId the ID of the user performing the update
   */
  @Override
  @Transactional
  public void update(UpdateGameObject dto, MultipartFile photo, int userId) {
    log.debug("Attempting to update game object with id {} by userId {}", dto.getId(), userId);

    var currentUser = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.error("User with  id {} not found", userId);
          return new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND);
        });

    var currentObject = gameObjectRepository.findById(dto.getId())
        .orElseThrow(() -> {
          log.error("Game object with Id {} not found", dto.getId());
          return new ResponseStatusException(BAD_REQUEST, GAME_NOT_FOUND);
        });

    if (!Objects.equals(currentObject.getUser().getId(), currentUser.getId())) {
      log.warn("User with id {} is not authorized to update game with id {}", userId, dto.getId());
      throw new ResponseStatusException(BAD_REQUEST, PERMISSION_DENIED_MESSAGE);
    }


    if (dto.getPrice() != 0 && dto.getPrice() != currentObject.getPrice()) {
      currentObject.setPrice(dto.getPrice());
    }

    if (dto.getTitle() != null && !dto.getTitle().equals(currentObject.getTitle())) {
      currentObject.setTitle(dto.getTitle());
    }

    if (dto.getText() != null && !dto.getText().equals(currentObject.getText())) {
      currentObject.setText(dto.getText());
    }


    if (photo != null && !photo.isEmpty()) {
      var oldPicture = currentObject.getPicture();
      var updatedPicture = updatePictureOnLocalFolder(oldPicture.getUrl(), userId, photo);

      if (updatedPicture == null) {
        log.error("Failed to update picture for game object with id {}", dto.getId());
        throw new ResponseStatusException(INTERNAL_SERVER_ERROR, PICTURE_CANNOT_BE_SAVED);
      }

      updatedPicture.setGameObject(currentObject);
      currentObject.setPicture(updatedPicture);
      currentObject.setUpdatedAt(LocalDateTime.now());

      gameObjectPictureRepository.delete(oldPicture);
      gameObjectRepository.save(currentObject);
      gameObjectPictureRepository.save(updatedPicture);
      log.info("Game object with id {} updated successfully by userId {}", dto.getId(), userId);
      return;
    }

    currentObject.setUpdatedAt(LocalDateTime.now());
    gameObjectRepository.save(currentObject);
    log.info("Game object with id {} updated successfully by userId {}", dto.getId(), userId);
  }


  /**
   * Retrieves all game objects for a specific seller.
   *
   * @param sellerId the ID of the seller
   * @return list of game object DTOs or empty
   */
  @Override
  public List<GameObjectDto> getGameObjectsBySellerId(int sellerId) {
    log.info("Attempting to fetch game objects for seller with ID: {}", sellerId);

    var user = userRepository.findById(sellerId)
        .orElseThrow(() -> {
          log.error("Seller with ID {} not found", sellerId);
          return new ResponseStatusException(BAD_REQUEST, SELLER_NOT_FOUND);
        });

    var gameEntities = gameObjectRepository.getGameObjectsBySellerId(sellerId);

    if (CollectionUtils.isEmpty(gameEntities)) {
      log.warn("No game objects found for seller with ID: {}", sellerId);
      return emptyList();
    }

    log.info("retrieved {} game objects for seller with ID: {}", gameEntities.size(), sellerId);
    return gameEntities.stream()
        .map(GameObjectDto::toDto)
        .toList();
  }


  /**
   * Searches game objects by title and/or seller rating.
   *
   * @param sellerRating the minimum rating of the seller
   * @param title        the title or partial title to filter by
   * @return list of filtered game object DTOs or empty list
   */
  @Override
  public List<GameObjectDto> searchGameObjects(int sellerRating, String title) {
    log.info("Searching game objects with seller rating: {} and title: {}", sellerRating, title);

    List<GameObject> data;

    if (title == null || title.isBlank()) {
      data = gameObjectRepository.filterBySellerRating(sellerRating);
      log.info("Filtering by seller rating: {}", sellerRating);
    } else {
      data = gameObjectRepository.filterByTitleAndRating(title, sellerRating);
      log.info("Filtering by title: {} and seller rating: {}", title, sellerRating);
    }

    if (CollectionUtils.isEmpty(data)) {
      log.warn("No game objects found with the given filters.");
      return emptyList();
    }

    log.info("Found {} game objects matching the criteria.", data.size());
    return data.stream()
        .map(GameObjectDto::toDto)
        .collect(Collectors.toList());
  }


  /**
   * Saves a new picture to the local file system under a user-specific directory.
   *
   * @param userId  the ID of the user
   * @param picture the image file to save
   * @return the saved {@link GameObjectPicture} entity, or null if saving fails
   */
  private GameObjectPicture saveNewPictureOnLocalFolder(int userId, MultipartFile picture) {
    log.info("Attempting to save new picture for user with ID: {}", userId);

    var userFolderPath = uploadDir + File.separator + userId + File.separator + "GAMES";
    var userFolder = new File(userFolderPath);
    if (!userFolder.exists()) {
      userFolder.mkdirs();
      log.info("Created directory for user pictures at: {}", userFolderPath);
    }

    var uuid = UUID.randomUUID();
    var modifiedFileName = uuid + picture.getOriginalFilename();
    var publicUrl = userId + File.separator + "GAMES" + File.separator + modifiedFileName;
    var filePath = userFolderPath + File.separator + modifiedFileName;

    try {
      var savedFile = new File(filePath);
      picture.transferTo(savedFile);
      log.info("saved picture with file name: {}", modifiedFileName);
      return GameObjectPicture.builder()
          .url(publicUrl)
          .size(picture.getSize())
          .extension(picture.getContentType())
          .photoName(modifiedFileName)
          .build();
    } catch (IOException e) {
      log.error("Failed to save picture for user with ID:{}. Error: {}", userId, e.getMessage());

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

    log.info("Updating picture for user with ID: {}. Current file URL: {}", userId, currentFileUrl);

    try {
      var path = Paths.get(uploadDir + File.separator + currentFileUrl);
      Files.delete(path);
      log.info("Deleted old picture at: {}", currentFileUrl);

      return saveNewPictureOnLocalFolder(userId, file);
    } catch (IOException e) {
      log.error("Failed to update picture for user with ID: {}. Error: {}", userId, e.getMessage());
      return null;
    }
  }
}
