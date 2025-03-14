package com.leverx.RatingSystemRest.Presentation.Dto;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder


public class GameObjectDto {


    public int id;
    public String publishDate;

    public String title;

    public String text;

    public double price;

    public String pictureUrl;

    public int sellerId;
    public String sellerFullName;

    public String sellerPictureUrl;


    public static GameObjectDto toDto(GameObject game, String sellerphoto, int sellerid, String FullName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return GameObjectDto.builder()
                .id(game.getId())
                .text(game.getText())
                .title(game.getTitle())
                .pictureUrl(game.getPicture().getUrl())
                .price(game.getPrice())
                .publishDate(game.getCreated_at().format(formatter))
                .build();


    }


//    public static GameObjectDto toProfileDto(GameObject game, String sellerphoto, int sellerid, String FullName) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        return GameObjectDto.builder()
//                .id(game.getId())
//                .text(game.getText())
//                .pictureUrl(game.getPicture().getUrl())
//                .SellerPictureUrl(sellerphoto)
//                .price(game.getPrice())
//                .sellerId(sellerid)
//                .Title(game.getTitle())
//                .publishDate(game.getCreated_at().format(formatter))
//                .SellerFullName(FullName).build();


 //   }

}
