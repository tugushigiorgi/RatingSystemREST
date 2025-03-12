package com.leverx.RatingSystemRest.Presentation.Dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class addGameObjectDto {

    public String title;

    public String text;

    public double price ;

    private MultipartFile picture ;



}
