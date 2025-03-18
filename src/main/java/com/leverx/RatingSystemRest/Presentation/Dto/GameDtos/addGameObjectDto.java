package com.leverx.RatingSystemRest.Presentation.Dto.GameDtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class addGameObjectDto {

    @NotBlank(message = "title field is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    public String title;

    @Size(min = 5, max = 1000, message = "Text must be between 5 and 1000 characters")
    @NotBlank(message = "text field is required")
    public String text;


   @NotNull(message = "price field is required")
    @Positive(message = "Price must be greater than zero")
    public double price;


}
