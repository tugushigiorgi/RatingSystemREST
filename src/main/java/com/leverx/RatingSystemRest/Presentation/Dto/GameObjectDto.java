package com.leverx.RatingSystemRest.Presentation.Dto;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameObjectDto {

    public String publishDate;

    public String Title;

    public String text;

    public double price;

    public String pictureUrl;

    public int sellerId;
    public String SellerFullName;

    public String SellerPictureUrl;



    public static GameObjectDto toDto(GameObject game, String sellerphoto, int sellerid, String FullName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
 return new GameObjectDto(game.getCreated_at().format(formatter), game.getTitle(),game.getText(),game.getPrice(),game.getPicture().getUrl(),sellerid,FullName,sellerphoto);
    }


}
