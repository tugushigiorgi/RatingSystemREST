package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Logindto {

    public String email;
    public String password;
}
