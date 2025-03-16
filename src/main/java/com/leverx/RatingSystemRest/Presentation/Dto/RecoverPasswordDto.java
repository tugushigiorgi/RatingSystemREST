package com.leverx.RatingSystemRest.Presentation.Dto;


import lombok.*;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class RecoverPasswordDto {

    public String token;
    public String password;

    public String newpassword;

}
