package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;


import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class AdminNotApprovedUserDto {


    public int id;
    public String pictureUrl;
    public String fullName;
    public String email;
    public String date;


    public static AdminNotApprovedUserDto toDto(User user ){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return AdminNotApprovedUserDto.builder()
                .id(user.getId())
                .date(user.getCreated_at().format(formatter))
                .email(user.getEmail())
                .fullName(user.fullName())
                .pictureUrl(user.getPhoto().getUrl()).build();


    }



}
