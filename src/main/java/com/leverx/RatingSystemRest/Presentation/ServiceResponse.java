package com.leverx.RatingSystemRest.Presentation;

import com.leverx.RatingSystemRest.Presentation.Dto.AuthDtos.JwtDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ServiceResponse {
  private boolean success;
  private String message;
  private HttpStatus status;
  private JwtDto jwt;
}
