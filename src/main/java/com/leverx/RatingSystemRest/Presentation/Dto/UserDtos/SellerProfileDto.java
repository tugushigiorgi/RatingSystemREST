package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Data
@Getter
@Setter
public class SellerProfileDto {

    public List<GameObjectDto> gameObjects;
    public UserInfoDto userInfo;


}
