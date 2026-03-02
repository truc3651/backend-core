package com.backend.core.cache.settings;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("localdevCacheConnectionSettingsProvider")
@ConditionalOnBean(LocaldevCacheSettings.class)
@RequiredArgsConstructor
public class CacheConnectionSettingsProviderImplLocaldev
    implements CacheConnectionSettingsProvider {
  private final LocaldevCacheSettings localdevCacheSettings;

  @Override
  public CacheConnectionSettings provide() {
    CacheConnectionSettings settings = new CacheConnectionSettings();
    settings.setHost(localdevCacheSettings.getHost());
    settings.setPort(localdevCacheSettings.getPort());
    settings.setPassword(null);
    settings.setTlsEnabled(false);
    return settings;
  }
}
