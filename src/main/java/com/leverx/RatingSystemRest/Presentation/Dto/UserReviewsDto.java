package com.leverx.RatingSystemRest.Presentation.Dto;

import lombok.*;

import java.time.LocalDateTime;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewsDto {


    public LocalDateTime PublishDate;

    public int Review;

    public String Comment;

    public String SellerFullName;

    public int id;

    public static UserReviewsDto toDto(Comment review) {

        return new UserReviewsDto(review.getCreated_at(), review.getRating(), review.getMessage(),review.getUser().fullName(),review.getId());

    }



}
