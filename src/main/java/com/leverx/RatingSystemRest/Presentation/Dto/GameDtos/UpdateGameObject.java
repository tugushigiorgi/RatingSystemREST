package com.leverx.RatingSystemRest.Presentation.Dto.GameDtos;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGameObject {

    public int Id;
    public String title;

    public String text;

    public double price ;

   // private MultipartFile picture ;
}
