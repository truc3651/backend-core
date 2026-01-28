package com.backend.core.security;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.backend.core.dtos.ValidateTokenRequestDto;
import com.backend.core.dtos.ValidateTokenResponseDto;

@FeignClient(name = "${services.users-management}", url = "${services.users-management.url}")
public interface UserClient {
  @PostMapping(path = "/v1/api/auth/validate-token")
  ValidateTokenResponseDto validateToken(ValidateTokenRequestDto request);
}
