package com.leverx.RatingSystemRest.Presentation.Dto.GameDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) used to represent the information required for adding a new game.
 * This DTO encapsulates the title, description (text), and price of the game that is being added
 * to the system. It is typically used to receive data from users when creating or adding a new game.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class addGameObjectDto {

  /**
   * The title of the game being added.
   * This field represents the name or title of the game. It is a required field and should be between
   * 1 and 100 characters in length.
   */
  @NotBlank(message = "title field is required")
  @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
  public String title;

  /**
   * The description or text about the game being added.
   * This field holds the details or description of the game. It is a required field and should be between
   * 1 and 1000 characters in length.
   */
  @Size(min = 1, max = 1000, message = "Text must be between 1 and 1000 characters")
  @NotBlank(message = "text field is required")
  public String text;

  /**
   * The price of the game being added.
   * This field represents the price of the game. It is a required field and must be a positive value
   * greater than zero.
   */
  @NotNull(message = "price field is required")
  @Positive(message = "Price must be greater than zero")
  public double price;

}
