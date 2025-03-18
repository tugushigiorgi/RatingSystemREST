package com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class addCommentDto {


    @Min(value = 1, message = "review must be at least 1")
    @Max(value = 5, message = "review cannot be greater than 5")
    @NotNull(message = " review field is  required")
    public int  review;

    @NotNull(message = "sellerId field is  required")
    public int sellerId;

    @NotBlank(message = "comment field is required")
    public String comment;

    @NotNull(message = "anonymousId field is  required")
    public int anonymousId;




}
