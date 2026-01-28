package com.backend.core.security;

import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Configuration
@ConfigurationProperties(prefix = "web.security")
public class SecuritySettings {
  private Set<OpenPath> openPaths;

  @Getter
  @Setter
  @NoArgsConstructor
  @ToString
  @Validated
  public static class OpenPath {
    @NotBlank private String pattern;
    private List<HttpMethod> methods;
  }
}
