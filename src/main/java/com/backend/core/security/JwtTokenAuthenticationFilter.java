package com.backend.core.security;

import java.util.Collections;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.backend.core.annotations.Anonymous;
import com.backend.core.dtos.ValidateTokenRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements WebFilter {
  private static final String BEARER = "Bearer ";
  private static final String ACTUATOR_ENDPOINT = "/actuator";
  private static final String HEALTH_ENDPOINT = "/health";
  private final UserClient userClient;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final RequestMappingHandlerMapping handlerMapping;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (isActuatorRequest(exchange)) {
      return chain.filter(exchange);
    }

    return isAnonymous(exchange)
        .filter(Boolean.TRUE::equals)
        .flatMap(ignored -> chain.filter(exchange))
        .switchIfEmpty(authenticate(exchange, chain));
  }

  private Mono<Void> authenticate(ServerWebExchange exchange, WebFilterChain chain) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (Objects.isNull(authHeader) || !authHeader.startsWith(BEARER)) {
      return chain.filter(exchange);
    }

    String token = authHeader.substring(BEARER.length());
    ValidateTokenRequestDto validateTokenRequest = new ValidateTokenRequestDto(token);

    return userClient
        .validateToken(validateTokenRequest)
        .flatMap(
            validationResult -> {
              if (Objects.nonNull(validationResult) && validationResult.isValid()) {
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        validationResult.getUser(), null, Collections.emptyList());
                return chain
                    .filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
              }
              return chain.filter(exchange);
            })
        .onErrorResume(
            e -> {
              log.error("Token validation failed: {}", e.getMessage());
              return authenticationEntryPoint.commence(
                  exchange,
                  new org.springframework.security.authentication.AuthenticationServiceException(
                      "Token validation failed: " + e.getMessage()));
            });
  }

  private Mono<Boolean> isAnonymous(ServerWebExchange exchange) {
    return handlerMapping
        .getHandler(exchange)
        .ofType(HandlerMethod.class)
        .map(this::hasAnonymousAnnotation)
        .defaultIfEmpty(false);
  }

  private boolean hasAnonymousAnnotation(HandlerMethod handlerMethod) {
    return handlerMethod.hasMethodAnnotation(Anonymous.class)
        || handlerMethod.getBeanType().isAnnotationPresent(Anonymous.class);
  }

  private boolean isActuatorRequest(ServerWebExchange exchange) {
    String path = exchange.getRequest().getURI().getPath();
    return path.startsWith(ACTUATOR_ENDPOINT) || path.startsWith(HEALTH_ENDPOINT);
  }
}
