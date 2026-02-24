package com.backend.core.security;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.backend.core.dtos.UserDto;
import com.backend.core.dtos.ValidateTokenRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter implements WebFilter {
  private static final String HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION;
  private static final String BEARER = "Bearer ";
  private final SecuritySettings securitySettings;
  private final UserClient userClient;
  private final CustomAuthenticationEntryPoint authenticationEntryPoint;
  private final AntPathMatcher pathMatcher = new AntPathMatcher();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (shouldNotFilter(exchange.getRequest())) {
      return chain.filter(exchange);
    }

    String authHeader = exchange.getRequest().getHeaders().getFirst(HEADER_AUTHORIZATION);

    if (Objects.isNull(authHeader) || !authHeader.startsWith(BEARER)) {
      return chain.filter(exchange);
    }

    String token = authHeader.substring(BEARER.length());
    ValidateTokenRequestDto validateTokenRequest = new ValidateTokenRequestDto(token);

    return userClient
        .validateToken(validateTokenRequest)
        .flatMap(
            validationResult -> {
              if (Objects.nonNull(validationResult)
                  && validationResult.isValid()
                  && validationResult.getId() != null) {
                UserDto user = new UserDto(validationResult.getId(), validationResult.getEmail());
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                return chain
                    .filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
              }
              // Returns Mono<Void> - another reactive publisher
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

  private boolean shouldNotFilter(ServerHttpRequest request) {
    Set<SecuritySettings.OpenPath> openPaths = securitySettings.getOpenPaths();
    if (CollectionUtils.isEmpty(openPaths)) {
      return false;
    }

    String requestPath = request.getURI().getPath();
    HttpMethod requestMethod = request.getMethod();

    return openPaths.stream()
        .anyMatch(
            openPath -> {
              if (CollectionUtils.isEmpty(openPath.getMethods())) {
                return pathMatcher.match(openPath.getPattern(), requestPath);
              }

              return openPath.getMethods().stream()
                  .anyMatch(
                      method ->
                          method.equals(requestMethod)
                              && pathMatcher.match(openPath.getPattern(), requestPath));
            });
  }
}
