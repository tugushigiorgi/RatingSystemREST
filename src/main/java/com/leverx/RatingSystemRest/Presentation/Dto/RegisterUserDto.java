package com.leverx.RatingSystemRest.Presentation.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public class RegisterUserDto {

    @NotNull
    public String name;
    @NotNull
    public String surname;
    @NotNull
    public String email;
    @NotNull
    public String password;
    @NotNull
    public String confirmPassword;


}
