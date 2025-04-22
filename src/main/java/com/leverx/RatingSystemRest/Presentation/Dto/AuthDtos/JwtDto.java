package com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a JSON Web Token (JWT) and the associated user role.
 * This DTO is used to encapsulate the JWT token and the role of the authenticated user.
 * It is typically returned in the response after a successful authentication.
 */
@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class JwtDto {

  /**
   * The JWT token that is issued after successful authentication.
   * This token is used to authenticate requests by including it in the Authorization header
   * as a Bearer token.
   */
  public String token;

  /**
   * The role of the user associated with the JWT token.
   * This field indicates the user's role (e.g., "ROLE_ADMIN", "ROLE_USER") and is used for authorization purposes.
   */
  public String role;
}
