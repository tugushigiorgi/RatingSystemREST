package com.leverx.RatingSystemRest.Presentation.Dto;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public class UserInfoDto {


    public String fullName;
    public String email;
    public String registretionDate;
    public String pictureUrl;
    public double rating;



    public static UserInfoDto toDto(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
       var totalRating= DetailedUserDto.CalculateTotalRatingComment(user.getComments());
        return UserInfoDto.builder()
                .email(user.getEmail())
                .pictureUrl(user.getPhoto().getUrl()).rating(totalRating)
                .registretionDate(user.getCreated_at().format(formatter))
                .fullName(user.fullName())
                .build();



    }

}
