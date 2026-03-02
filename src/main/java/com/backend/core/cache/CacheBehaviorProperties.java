package com.backend.core.cache;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.cache")
public class CacheBehaviorProperties {
  private String keyPrefix = "entity:";
  private Duration ttl = Duration.ofMinutes(10);
  private boolean enabled = true;
}
