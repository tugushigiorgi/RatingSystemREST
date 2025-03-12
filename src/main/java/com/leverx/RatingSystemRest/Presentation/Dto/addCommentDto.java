package com.leverx.RatingSystemRest.Presentation.Dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
