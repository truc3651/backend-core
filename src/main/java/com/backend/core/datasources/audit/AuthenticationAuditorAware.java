package com.backend.core.datasources.audit;

import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.stereotype.Component;

import com.backend.core.dtos.UserDto;
import com.backend.core.security.JwtTokenAuthenticationHolder;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationAuditorAware implements ReactiveAuditorAware<Long> {
  @Override
  public Mono<Long> getCurrentAuditor() {
    return JwtTokenAuthenticationHolder.findAuthenticatedUser()
        .map(UserDto::getId)
        .defaultIfEmpty(0L);
  }
}
