package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class SellerProfileDto {

    public List<GameObjectDto> gameObjects;
    public UserInfoDto userInfo;


}
