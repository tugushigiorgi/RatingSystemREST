package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;


import lombok.*;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class jwtDto {
    public String token;
    public String role;
}
