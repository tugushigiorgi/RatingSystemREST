package com.leverx.RatingSystemRest.Presentation.Dto.UserDtos;

import com.leverx.RatingSystemRest.Presentation.Dto.GameDtos.GameObjectDto;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object representing a seller's profile.
 * Contains a list of game objects being sold by the user
 * and user-specific profile information.
 */
@Builder
@Data
@Getter
@Setter
public class SellerProfileDto {

  /**
   * List of game objects associated with the seller.
   */
  public List<GameObjectDto> gameObjects;

  /**
   * User information associated with the seller.
   */
  public UserInfoDto userInfo;

}
