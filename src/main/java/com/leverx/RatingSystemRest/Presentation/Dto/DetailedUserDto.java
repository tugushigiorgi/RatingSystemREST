package com.leverx.RatingSystemRest.Presentation.Dto;


import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;
import com.leverx.RatingSystemRest.Infrastructure.Entities.User;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Builder
@Data
public class DetailedUserDto {

    private int id;
    private String pictureUrl;
    private String fullName;
    private String email;
    private String date;
    private double totalRating;
    private List<UserReviewsDto> reviews;

    public static DetailedUserDto toDetailedUserDto(User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        var reviewsList = user.getComments().stream()
                .filter(Comment::isApproved)
                .map(UserReviewsDto::toDto)
                .toList();


        var totalRating = CalculateTotalRatingDto(reviewsList);

        return DetailedUserDto.builder()
                .id(user.getId())
                .pictureUrl(user.getPhoto().getUrl())
                .fullName(user.fullName())
                .email(user.getEmail())
                .date(user.getCreated_at().format(formatter))
                .totalRating(totalRating)
                .reviews(reviewsList)
                .build();

}
    public static double  CalculateTotalRatingDto(List<UserReviewsDto> comment) {
        int totalRatingCount = comment.size();
        if (totalRatingCount == 0) {
            return 0;
        }

        double sumRating = comment.stream()
                .mapToInt(UserReviewsDto::getReview)
                .sum();
        return  sumRating / totalRatingCount;


    }

    public static double  CalculateTotalRatingComment(List<Comment> comment) {
        int totalRatingCount = comment.size();
        if (totalRatingCount == 0) {
            return 0;
        }

        double sumRating = comment.stream()
                .mapToInt(Comment::getRating)
                .sum();
        return  sumRating / totalRatingCount;


    }



}
