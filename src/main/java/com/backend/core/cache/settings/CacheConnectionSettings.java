package com.backend.core.cache.settings;

import lombok.Data;

@Data
public class CacheConnectionSettings {
  private String host;
  private String port;
  private String password;
  private boolean tlsEnabled;
}
