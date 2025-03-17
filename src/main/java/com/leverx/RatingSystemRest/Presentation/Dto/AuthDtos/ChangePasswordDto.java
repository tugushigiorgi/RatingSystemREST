package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;

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
