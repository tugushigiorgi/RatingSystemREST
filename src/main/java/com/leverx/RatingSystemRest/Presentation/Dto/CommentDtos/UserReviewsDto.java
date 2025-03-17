package com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos;

import lombok.*;

import java.time.format.DateTimeFormatter;

import com.leverx.RatingSystemRest.Infrastructure.Entities.Comment;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReviewsDto {

    private String publishDate;
    private int review;
    private String comment;
    private String sellerFullName;
    private int id;

    public static UserReviewsDto toDto(Comment review) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return UserReviewsDto.builder()
                .publishDate(review.getCreated_at().format(formatter))
                .review(review.getRating())
                .comment(review.getMessage())
                .sellerFullName(review.getUser().fullName())
                .id(review.getId())
                .build();
    }
}
