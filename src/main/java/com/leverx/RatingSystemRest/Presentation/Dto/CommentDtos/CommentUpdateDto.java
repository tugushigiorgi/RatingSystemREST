package com.leverx.RatingSystemRest.Presentation.Dto.CommentDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDto {

    @NotNull(message = "comment id required")
    public int commentId;

    @NotNull(message = "review filed required")
    public int  review;

    @NotBlank(message = "sellerId field is required")
    public int sellerId;

    @NotBlank(message = "comment field is required")
    public String comment;

    @NotNull(message = "anonymousId field is  required")
    public int anonymousId;

}
