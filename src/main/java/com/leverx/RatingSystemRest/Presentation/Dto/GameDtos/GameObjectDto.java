package com.leverx.RatingSystemRest.Presentation.Dto.GameDtos;

import com.leverx.RatingSystemRest.Infrastructure.Entities.GameObject;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used to represent the details of a game object.
 * This DTO is typically used to provide the details of a game, such as its title, description,
 * price, and seller information, in a structured format for transferring data between systems.
 */
@Builder
@Getter
@Setter
public class GameObjectDto {

  /**
   * The unique identifier of the game object.
   */
  public int id;

  /**
   * The publish date of the game object.
   * This field represents the date when the game was created or published. It is formatted as "dd/MM/yyyy".
   */
  public String publishDate;

  /**
   * The title of the game object.
   * This field holds the name or title of the game.
   */
  public String title;

  /**
   * The description of the game object.
   * This field contains a detailed description or text about the game.
   */
  public String text;

  /**
   * The price of the game object.
   * This field represents the price of the game. It is represented as a decimal value.
   */
  public double price;

  /**
   * The URL of the picture associated with the game object.
   * This field holds the URL of the image representing the game.
   */
  public String pictureUrl;

  /**
   * The unique identifier of the seller of the game.
   * This field holds the ID of the user who is selling the game.
   */
  public int sellerId;

  /**
   * The full name of the seller of the game.
   * This field contains the full name of the user who is selling the game.
   */
  public String sellerFullName;

  /**
   * The URL of the picture associated with the seller.
   * This field holds the URL of the profile picture of the user selling the game.
   */
  public String sellerPictureUrl;

  /**
   * Converts a `GameObject` entity to a `GameObjectDto`.
   * <p>
   * This method is used to map the data from a `GameObject` entity to a `GameObjectDto` object.
   * It extracts the game details, seller information, and formats the publish date.
   *
   * @param game The `GameObject` entity to be converted.
   * @return A `GameObjectDto` object containing the game details and seller information.
   */
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
        .publishDate(game.getCreatedAt().format(formatter))
        .build();
  }

}
