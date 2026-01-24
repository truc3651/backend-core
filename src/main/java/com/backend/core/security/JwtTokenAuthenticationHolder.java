package com.backend.core.security;

import lombok.experimental.UtilityClass;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.backend.core.dtos.UserDto;

@UtilityClass
public class JwtTokenAuthenticationHolder {
    public static Optional<UserDto> findAuthenticatedUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(UserDto.class::cast);
    }

    public static UserDto getAuthenticatedUser() {
        return findAuthenticatedUser()
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Not Authorized"));
    }
}
