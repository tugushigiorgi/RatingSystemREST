package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class jwtDto {
    public String token;
    public String role;
}
