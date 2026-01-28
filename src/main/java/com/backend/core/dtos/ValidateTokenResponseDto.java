package com.backend.core.dtos;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ValidateTokenResponseDto extends UserDto {
  private boolean valid;
  private Date expiresAt;
}
