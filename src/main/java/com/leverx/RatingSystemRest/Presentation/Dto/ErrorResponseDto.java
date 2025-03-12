package com.leverx.RatingSystemRest.Presentation.Dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorResponseDto {

    public String Error;

    public String Message;

}
