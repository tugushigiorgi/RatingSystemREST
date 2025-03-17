package com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class addCommentDto {


    public int  review;

    public int sellerId;

    public String comment;

    public int anonymousId;




}
