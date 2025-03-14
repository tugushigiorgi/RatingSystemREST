package com.leverx.RatingSystemRest.Presentation.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {


    public String oldPassword;

    public String newPassword;

    public String repeatPassword;




}
