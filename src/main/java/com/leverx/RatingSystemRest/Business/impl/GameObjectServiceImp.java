package com.leverx.RatingSystemRest.Business.impl;

import com.leverx.RatingSystemRest.Business.Interfaces.GameObjectService;
import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObjectPicture;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectPictureRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserPhotoRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.UpdateGameObject;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.addGameObjectDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.leverx.RatingSystemRest.Business.ConstMessages.CommentConstMessages.PERMISSION_DENIED_MESSAGE;
import static com.leverx.RatingSystemRest.Business.ConstMessages.FileConstMessages.PICTURE_CANNOT_BE_SAVED;
import static com.leverx.RatingSystemRest.Business.ConstMessages.GameObjectMessages.*;
import static com.leverx.RatingSystemRest.Business.ConstMessages.UserConstMessages.SELLER_NOT_FOUND;

@Data
@Service
//TODO DOUBLE CHECK THIS ISSUE LETTER
public class GameObjectServiceImp  implements GameObjectService {
    //TODO ADD ALL FINAL MODIFER WHEN POSSIBLE
    private final UserPhotoRepository userPhotoRepository;
    private final GameObjectRepository gameObjectRepository;
    private final UserRepository userRepository;
    private final GameObjectPictureRepository gameObjectPictureRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public GameObjectServiceImp(GameObjectRepository gameObjectRepository, UserRepository userRepository, UserPhotoRepository userPhotoRepository, GameObjectPictureRepository gameObjectPictureRepository) {
        this.gameObjectRepository = gameObjectRepository;
        this.userRepository = userRepository;
        this.userPhotoRepository = userPhotoRepository;
        this.gameObjectPictureRepository = gameObjectPictureRepository;
    }

    public ResponseEntity<String> add(addGameObjectDto dto, MultipartFile photo, int userId) {
        var currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, SELLER_NOT_FOUND));
        var savedPicture = saveNewPictureOnLocalFolder(userId, photo);
        if (savedPicture == null) {
            return new ResponseEntity<>(PICTURE_CANNOT_BE_SAVED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        var gameObject = GameObject.builder()
                .price(dto.getPrice())
                .title(dto.getTitle())
                .text(dto.getText())
                .user(currentUser)
                .created_at(LocalDateTime.now())
                .picture(savedPicture)
                .build();
        savedPicture.setGameObject(gameObject);
        gameObjectRepository.save(gameObject);

        return new ResponseEntity<>(GAME_ADDED, HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<String> remove(int gameObjectId, int userId) {

        var getcurrentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, SELLER_NOT_FOUND));

        var currentObject = gameObjectRepository.findById(gameObjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, GAME_NOT_FOUND));

        if (!Objects.equals(currentObject.getUser().getId(), currentObject.getUser().getId())) {

            return new ResponseEntity<>(PERMISSION_DENIED_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        try {
            gameObjectRepository.delete(currentObject);
           var path = Paths.get(uploadDir + File.separator + currentObject.getPicture().getUrl());
            Files.delete(path);
            return new ResponseEntity<>(SUCCESSFULLY_DELETED_WITH_ID + gameObjectId, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }


    public ResponseEntity<String> update(UpdateGameObject dto, MultipartFile photo, int userId) {

        var currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, SELLER_NOT_FOUND));

        var currentObject = gameObjectRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, GAME_NOT_FOUND));


        if (currentObject.getUser().getId() != currentObject.getUser().getId()) {

            return new ResponseEntity<>(PERMISSION_DENIED_MESSAGE, HttpStatus.BAD_REQUEST);
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
                currentObject.setUpdated_at(LocalDateTime.now());
                updatePicture.setGameObject(currentObject);
                gameObjectPictureRepository.delete(currentObject.getPicture());
                gameObjectRepository.save(currentObject);
                gameObjectPictureRepository.save(updatePicture);
                return new ResponseEntity<>(OBJECT_UPDATED, HttpStatus.OK);

            }


        }
        currentObject.setUpdated_at(LocalDateTime.now());
        gameObjectRepository.save(currentObject);


        return new ResponseEntity<>(OBJECT_UPDATED, HttpStatus.OK);

    }


    private GameObjectPicture updatePictureOnLocalFolder(String CurrentFileUrl, int userid, MultipartFile file) {
        try {
           var  path = Paths.get(uploadDir + File.separator + CurrentFileUrl);
            Files.delete(path);
            return saveNewPictureOnLocalFolder(userid, file);
        } catch (IOException e) {
            return null;
        }


    }


    private GameObjectPicture saveNewPictureOnLocalFolder(int userid, MultipartFile picture) {
        var  userFolderPath = uploadDir + File.separator + userid + File.separator + "GAMES";

        var  userFolder = new File(userFolderPath);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
        var  uuid = UUID.randomUUID();
        var modifedFileName = uuid + picture.getOriginalFilename();
        var publicUrl = userid + File.separator + "GAMES" + File.separator + modifedFileName;
        var  filePath = userFolderPath + File.separator + modifedFileName;
        try {

            var savedFile = new File(filePath);
            picture.transferTo(savedFile);
            return GameObjectPicture.builder()
                    .Url(publicUrl)
                    .size(picture.getSize())
                    .Extension(picture.getContentType())
                    .photoName(modifedFileName).build();

        } catch (IOException e) {


            e.printStackTrace();
        }
        return null;
    }


    public ResponseEntity<List<GameObjectDto>> getGameObjectsBySellerId(int sellerId) throws Exception {

        var getUser = userRepository.findById(sellerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, SELLER_NOT_FOUND));

        var getGames = gameObjectRepository.getGameObjectsBySellerId(sellerId);

        if (CollectionUtils.isEmpty(getGames)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        var toDto = getGames.stream().map(GameObjectDto::toDto).toList();
        return ResponseEntity.ok(toDto);

    }


    public ResponseEntity<List<GameObjectDto>> searchGameObjects(int sellerRating, String title) {
        List<GameObject> data;

        if (title == null || title.isBlank()) {
            data = gameObjectRepository.filterBySellerRating(sellerRating);
        } else {
            data = gameObjectRepository.filterByTitleAndRating(title, sellerRating);
        }

        if (CollectionUtils.isEmpty(data)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        var  dtoList = data.stream()
                .map(GameObjectDto::toDto)
                .toList();

        return ResponseEntity.ok(dtoList);


    }


}




