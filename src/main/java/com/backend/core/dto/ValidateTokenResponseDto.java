package com.backend.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ValidateTokenResponseDto extends UserDto {
    private boolean valid;
    private Date expiresAt;
}
