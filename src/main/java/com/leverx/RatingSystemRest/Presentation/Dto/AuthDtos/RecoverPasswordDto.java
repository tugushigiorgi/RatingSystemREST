package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class RecoverPasswordDto {

    @NotBlank(message = "Token is required")
    public String token;
    @NotBlank(message = "password field is required")
    public String password;
    @NotBlank(message = "repeat password field is required")
    public String newpassword;

}
