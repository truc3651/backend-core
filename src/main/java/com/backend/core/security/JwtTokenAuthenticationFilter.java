package com.backend.core.security;

import com.backend.core.dto.UserDto;
import com.backend.core.dto.ValidateTokenRequestDto;
import com.backend.core.dto.ValidateTokenResponseDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private final SecuritySettings securitySettings;
    private final UserClient userClient;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader(HEADER_AUTHORIZATION);

        if (Objects.isNull(authHeader) || !authHeader.startsWith(BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(BEARER.length());
            ValidateTokenRequestDto validateTokenRequest = new ValidateTokenRequestDto(token);
            ValidateTokenResponseDto validationResult = userClient.validateToken(validateTokenRequest);

            if (Objects.nonNull(validationResult) && validationResult.isValid() && validationResult.getId() != null) {
                UserDto user = new UserDto(validationResult.getId(), validationResult.getEmail());
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
        } catch (AuthenticationException authException) {
            authenticationEntryPoint.commence(request, response, authException);
        } catch (Exception e) {
            AuthenticationException authException = new AuthenticationException("Token validation failed: " + e.getMessage()) {};
            authenticationEntryPoint.commence(request, response, authException);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        Set<SecuritySettings.OpenPath> openPaths = securitySettings.getOpenPaths();
        if (CollectionUtils.isEmpty(openPaths)) {
            return false;
        }

        return openPaths.stream().anyMatch(openPath -> {
            if (CollectionUtils.isEmpty(openPath.getMethods())) {
                AntPathRequestMatcher matcher = new AntPathRequestMatcher(openPath.getPattern());
                return matcher.matches(request);
            }

            return openPath.getMethods().stream()
                    .anyMatch(method -> {
                        AntPathRequestMatcher methodMatcher = new AntPathRequestMatcher(
                                openPath.getPattern(),
                                method.name()
                        );
                        return methodMatcher.matches(request);
                    });
        });
    }
}
