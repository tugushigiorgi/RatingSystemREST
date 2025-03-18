package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDto {

    @NotBlank(message = "Old password is required")
    @Size(min = 6, message = "old password field must be at least 6 characters long")
    public String oldPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "new  password field must be at least 6 characters long")
    public String newPassword;

    @NotBlank(message = "Repeat password is required")
    @Size(min = 6, message = "repeat password field must be at least 6 characters long")
    public String repeatPassword;

}
