package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;


import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos.UserReviewsDto;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Builder
@Data
@Getter
@Setter
public class DetailedUserDto {

    private int id;
    private String pictureUrl;
    private String fullName;
    private String email;
    private String date;
    private String  totalRating;
    private List<UserReviewsDto> reviews;

    public static DetailedUserDto toDetailedUserDto(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var reviewsList = user.getComments().stream()
                .filter(Comment::isApproved)
                .map(UserReviewsDto::toDto)
                .toList();
        return DetailedUserDto.builder()
                .id(user.getId())
                .pictureUrl(user.getPhoto().getUrl())
                .fullName(user.fullName())
                .email(user.getEmail())
                .date(user.getCreated_at().format(formatter))
                .totalRating(String.format("%.2f", user.getTotalRating()))
                .reviews(reviewsList)
                .build();

}






}
