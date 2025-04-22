package com.leverx.RatingSystemRest.Presentation.Dto.GameDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for updating a game object.
 * This DTO is used to carry the data for updating an existing game object, including the game's title,
 * description, and price. It includes validation constraints to ensure proper values are provided for each field.
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGameObject {

  /**
   * The unique identifier of the game object.
   * This field is required for identifying which game object to update.
   */
  @NotNull(message = "id field is required")
  public int id;

  /**
   * The title of the game object.
   * This field is required and cannot be empty.
   */
  @NotBlank(message = "title field is required")
  public String title;

  /**
   * The description of the game object.
   * This field is required and cannot be empty.
   */
  @NotBlank(message = "text field is required")
  public String text;

  /**
   * The price of the game object.
   * This field is required and must be greater than zero.
   */
  @NotNull(message = "price field is required")
  @Positive(message = "price must be greater than zero")
  public double price;

}
