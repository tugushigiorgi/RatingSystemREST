package com.leverx.RatingSystemRest.Presentation.Dto.GameDtos;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Builder
@Getter
@Setter
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


    public static GameObjectDto toDto(GameObject game) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return GameObjectDto.builder()
                .id(game.getId())
                .sellerId(game.getUser().getId())
                .sellerFullName(game.getUser().fullName())
                .sellerPictureUrl(game.getUser().getPhoto().getUrl())
                .text(game.getText())
                .title(game.getTitle())
                .pictureUrl(game.getPicture().getUrl())
                .price(game.getPrice())
                .publishDate(game.getCreated_at().format(formatter))
                .build();


    }


}
