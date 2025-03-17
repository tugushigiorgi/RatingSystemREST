package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class isAdminDto {

    public boolean isAdmin;
}
