package com.leverx.RatingSystemRest.Presentation.Dto.GameDtos;


import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class addGameObjectDto {

    public String title;

    public String text;

    public double price ;

 //   public  MultipartFile photo;



}
