package com.backend.core.datasources.audit;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.backend.core.dtos.UserDto;
import com.backend.core.security.JwtTokenAuthenticationHolder;

@Component
public class AuthenticationAuditorAware implements AuditorAware<Long> {
  @Override
  public Optional<Long> getCurrentAuditor() {
    return JwtTokenAuthenticationHolder.findAuthenticatedUser().map(UserDto::getId);
  }
}
