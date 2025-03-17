package com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDto {


    public int commentId;

    public int  review;

    public int sellerId;

    public String comment;

    public int anonymousId;








}
