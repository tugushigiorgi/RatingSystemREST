package com.leverx.RatingSystemRest.Presentation.Dto;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewsDto {


    public String  PublishDate;

    public int Review;

    public String Comment;

    public String SellerFullName;

    public int id;

    public static UserReviewsDto toDto(Comment review) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new UserReviewsDto(review.getCreated_at().format(formatter), review.getRating(), review.getMessage(),review.getUser().fullName(),review.getId());

    }



}
