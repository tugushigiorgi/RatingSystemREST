package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public class UserInfoDto {

    public int id;
    public String fullName;
    public String email;
    public String registretionDate;
    public String pictureUrl;
    public String rating;


    public static UserInfoDto toDto(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return UserInfoDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .pictureUrl(user.getPhoto().getUrl())
                .rating(String.format("%.2f", user.getTotalRating()))
                .registretionDate(user.getCreated_at().format(formatter))
                .fullName(user.fullName())
                .build();


    }

}
