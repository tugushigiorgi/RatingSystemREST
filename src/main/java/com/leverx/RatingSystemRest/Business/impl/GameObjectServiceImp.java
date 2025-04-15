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

@Data
@Service
//TODO DOUBLE CHECK THIS ISSUE LETTER
public class GameObjectServiceImp  implements GameObjectService {
    //TODO ADD ALL FINAL MODIFER WHEN POSSIBLE
    private final UserPhotoRepository userPhotoRepository;
    private GameObjectRepository gameObjectRepository;
    private UserRepository userRepository;
    private GameObjectPictureRepository gameObjectPictureRepository;
    private UserPhotoRepository UserPhotoRepository;
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller not found"));
        var savedPicture = saveNewPictureOnLocalFolder(userId, photo);
        if (savedPicture == null) {
            return new ResponseEntity<>("Failed to save picture.", HttpStatus.INTERNAL_SERVER_ERROR);
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

        return new ResponseEntity<>("New object added successfully!", HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<String> remove(int gameObjectId, int userId) {

        var getcurrentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller not found"));

        var currentObject = gameObjectRepository.findById(gameObjectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "GameObject   not found"));

        if (!Objects.equals(currentObject.getUser().getId(), currentObject.getUser().getId())) {

            return new ResponseEntity<>("Permission denied", HttpStatus.BAD_REQUEST);
        }

        try {
            gameObjectRepository.delete(currentObject);
            Path path = Paths.get(uploadDir + File.separator + currentObject.getPicture().getUrl());
            Files.delete(path);
            return new ResponseEntity<>("Successfully deleted with id" + gameObjectId, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }


    public ResponseEntity<String> update(UpdateGameObject dto, MultipartFile photo, int userId) {

        var currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller not found"));

        var currentObject = gameObjectRepository.findById(dto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "GameObject not found"));


        if (currentObject.getUser().getId() != currentObject.getUser().getId()) {

            return new ResponseEntity<>("Permission denied", HttpStatus.BAD_REQUEST);
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
                return new ResponseEntity<>("object updated succesfully", HttpStatus.OK);

            }


        }
        currentObject.setUpdated_at(LocalDateTime.now());
        gameObjectRepository.save(currentObject);


        return new ResponseEntity<>("Game object updated successfully", HttpStatus.OK);

    }


    private GameObjectPicture updatePictureOnLocalFolder(String CurrentFileUrl, int userid, MultipartFile file) {
        try {
            Path path = Paths.get(uploadDir + File.separator + CurrentFileUrl);
            Files.delete(path);
            return saveNewPictureOnLocalFolder(userid, file);
        } catch (IOException e) {
            return null;
        }


    }


    private GameObjectPicture saveNewPictureOnLocalFolder(int userid, MultipartFile picture) {
        String userFolderPath = uploadDir + File.separator + userid + File.separator + "GAMES";

        File userFolder = new File(userFolderPath);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
        UUID uuid = UUID.randomUUID();
        String modifedFileName = uuid + picture.getOriginalFilename();
        String publicUrl = userid + File.separator + "GAMES" + File.separator + modifedFileName;
        String filePath = userFolderPath + File.separator + modifedFileName;
        try {

            File savedFile = new File(filePath);
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seller not found"));

        var getGames = gameObjectRepository.getGameObjectsBySellerId(sellerId);

        if (getGames == null || getGames.isEmpty()) {
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

        if (data== null || data.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<GameObjectDto> dtoList = data.stream()
                .map(GameObjectDto::toDto)
                .toList();

        return ResponseEntity.ok(dtoList);


    }


}




