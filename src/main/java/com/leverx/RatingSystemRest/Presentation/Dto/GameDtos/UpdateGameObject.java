package com.leverx.RatingSystemRest.Presentation.Dto.GameDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGameObject {

    @NotNull(message = "id field is required")
    public int Id;
    @NotBlank(message = "title field is required")
    public String title;
    @NotBlank(message = "text field is required")
    public String text;
    @NotNull(message = "price field is required")
    @Positive(message = "price must be greater than zero")
    public double price ;


}
