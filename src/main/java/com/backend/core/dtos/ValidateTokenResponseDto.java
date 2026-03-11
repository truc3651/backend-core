package com.backend.core.dtos;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ValidateTokenResponseDto {
  private boolean valid;
  private Date expiresAt;
  private UserDto user;
}
