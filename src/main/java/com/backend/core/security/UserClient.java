package com.backend.core.security;

import com.backend.core.dto.ValidateTokenRequestDto;
import com.backend.core.dto.ValidateTokenResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "${services.users-management}", url = "${services.users-management.url}")
public interface UserClient {
    @PostMapping(path = "/v1/api/auth/validate-token")
    ValidateTokenResponseDto validateToken(ValidateTokenRequestDto request);
}
