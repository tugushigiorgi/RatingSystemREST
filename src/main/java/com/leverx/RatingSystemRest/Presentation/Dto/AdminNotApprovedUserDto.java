package com.leverx.RatingSystemRest.Presentation.Dto;


import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class AdminNotApprovedUserDto {


    public int id;
    public String PictureUrl;
    public String FullName;
    public String Email;
    public String Date;


    public static AdminNotApprovedUserDto toDto(User user ){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return AdminNotApprovedUserDto.builder()
                .id(user.getId())
                .Date(user.getCreated_at().format(formatter))
                .Email(user.getEmail())
                .FullName(user.fullName())
                .PictureUrl(user.getPhoto().getUrl()).build();


    }



}
