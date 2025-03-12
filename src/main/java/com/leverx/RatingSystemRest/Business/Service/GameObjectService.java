package com.leverx.RatingSystemRest.Business.Service;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObjectPicture;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectPictureRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserPhotoRepository;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.UserRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.GameObjectDto;
import com.leverx.RatingSystemRest.Presentation.Dto.UpdateGameObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import com.leverx.RatingSystemRest.Infrastructure.Repositories.GameObjectRepository;
import com.leverx.RatingSystemRest.Presentation.Dto.addGameObjectDto;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@Service
public class GameObjectService {
    private final UserPhotoRepository userPhotoRepository;
    private GameObjectRepository gameObjectRepository;
    private UserRepository userRepository;
    private GameObjectPictureRepository gameObjectPictureRepository;
    private UserPhotoRepository UserPhotoRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;

    public GameObjectService(GameObjectRepository gameObjectRepository, UserRepository userRepository, UserPhotoRepository userPhotoRepository) {
        this.gameObjectRepository = gameObjectRepository;
        this.userRepository = userRepository;
        this.userPhotoRepository = userPhotoRepository;
    }

    public ResponseEntity<String> add(addGameObjectDto dto, MultipartFile photo,int userId) {
        var getcurrentUser = userRepository.findById(userId);
        if (getcurrentUser.isPresent()) {
            var currentUser = getcurrentUser.get();
            String userEmail = currentUser.getEmail();
            var savepicture = saveNewPictureOnLocalFolder(userEmail, photo);
            if (savepicture != null) {
                var gameObject = GameObject.builder()
                        .price(dto.getPrice())
                        .title(dto.getTitle())
                        .text(dto.getText())
                        .user(currentUser)
                        .created_at(LocalDateTime.now())
                        .picture(savepicture).build();

                savepicture.setGameObject(gameObject);

                gameObjectRepository.save(gameObject);

                return new ResponseEntity<>("new object added succesfully", HttpStatus.OK);

            }

        }
        return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);


    }


    public ResponseEntity<String> remove(int gameObjectId, int userId) {

        var getcurrentUser = userRepository.findById(userId);
        var getcurrentGameObject = gameObjectRepository.findById(gameObjectId);
        if (getcurrentUser.isEmpty() || getcurrentGameObject.isEmpty()) {

            return new ResponseEntity<>("User/Game not found", HttpStatus.BAD_REQUEST);

        }

        var currentObject = getcurrentGameObject.get();

        if (currentObject.getUser().getId() != currentObject.getId()) {

            return new ResponseEntity<>("Permission denied", HttpStatus.BAD_REQUEST);
        }

        try {
            gameObjectRepository.delete(currentObject);
            return new ResponseEntity<>("Succesfully deleted with id" + gameObjectId, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

        }


    }


    public ResponseEntity<String> update(UpdateGameObject dto, int userId) {

        var getcurrentUser = userRepository.findById(userId);
        var getcurrentGameObject = gameObjectRepository.findById(dto.getId());
        if (getcurrentUser.isEmpty() || getcurrentGameObject.isEmpty()) {

            return new ResponseEntity<>("User/Game not found", HttpStatus.BAD_REQUEST);

        }
        var currentUser = getcurrentUser.get();
        var currentObject = getcurrentGameObject.get();
        if (currentObject.getUser().getId() != currentObject.getId()) {

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
        if (dto.getPicture() != null) {


            var updatePicture = UpdatePictureOnLocalFolder(currentObject.getPicture().getUrl(), currentUser.getEmail(), dto.getPicture());
            if (updatePicture != null) {


                currentObject.setPicture(updatePicture);

                updatePicture.setGameObject(currentObject);
                gameObjectPictureRepository.delete(currentObject.getPicture());
                gameObjectRepository.save(currentObject);
                gameObjectPictureRepository.save(updatePicture);


                return new ResponseEntity<>("object updated succesfully", HttpStatus.OK);

            }

        }
        return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

    }


    private GameObjectPicture UpdatePictureOnLocalFolder(String CurrentFileUrl, String userEmail, MultipartFile file) {


        try {
            Path path = Paths.get(CurrentFileUrl);
            Files.delete(path);

            return saveNewPictureOnLocalFolder(userEmail, file);


        } catch (IOException e) {
            return null;
        }


    }


    private GameObjectPicture saveNewPictureOnLocalFolder(String userEmail, MultipartFile picture) {
        String userFolderPath = uploadDir + File.separator + userEmail + File.separator + "profile";

       System.out.println(userFolderPath);
        File userFolder = new File(userFolderPath);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }
        UUID uuid = UUID.randomUUID();
        String filePath = userFolderPath + File.separator + uuid+picture.getOriginalFilename() ;
        try {

            File savedFile = new File(filePath);
            picture.transferTo(savedFile);
            return GameObjectPicture.builder()
                    .Url(filePath)
                    .size(picture.getSize())
                    .Extension(picture.getContentType())
                    .photoName(picture.getOriginalFilename()).build();

        } catch (IOException e) {


            e.printStackTrace();
        }
        return null;
    }


    public List<GameObjectDto> getGameObjectsBySellerId(int sellerId) throws Exception {

        var getUser = userRepository.findById(sellerId);
        if (getUser.isEmpty()) {

            throw new Exception("User not found");
        }
        var currentUser = getUser.get();

        var getGames = gameObjectRepository.getGameObjectsBySellerId(sellerId);

        return getGames.stream().map(game -> GameObjectDto.toDto(game,currentUser.getPhoto().getUrl(),sellerId,currentUser.fullName())).toList();





    }









}




