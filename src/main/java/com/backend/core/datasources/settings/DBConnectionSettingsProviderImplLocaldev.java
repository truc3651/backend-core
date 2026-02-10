package com.backend.core.datasources.settings;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component("localdevDBConnectionSettingsProvider")
@ConditionalOnBean(LocaldevDatabaseSettings.class)
@RequiredArgsConstructor
public class DBConnectionSettingsProviderImplLocaldev implements DBConnectionSettingsProvider {
  private final LocaldevDatabaseSettings localdevDatabaseSettings;

  @Override
  public DBConnectionSettings provide() {
    DBConnectionSettings dbConnectionSettings = new DBConnectionSettings();

    // Writer
    dbConnectionSettings.setWriterHost(localdevDatabaseSettings.getAuroraHost());
    dbConnectionSettings.setWriterPort(localdevDatabaseSettings.getAuroraPort());
    dbConnectionSettings.setWriterUsername(localdevDatabaseSettings.getAuroraUsername());
    dbConnectionSettings.setWriterPassword(localdevDatabaseSettings.getAuroraPassword());

    // Reader
    dbConnectionSettings.setReaderHost(localdevDatabaseSettings.getAuroraHost());
    dbConnectionSettings.setReaderPort(localdevDatabaseSettings.getAuroraPort());
    dbConnectionSettings.setReaderUsername(localdevDatabaseSettings.getAuroraUsername());
    dbConnectionSettings.setReaderPassword(localdevDatabaseSettings.getAuroraPassword());

    // Migration
    dbConnectionSettings.setMigrationUsername(localdevDatabaseSettings.getAuroraUsername());
    dbConnectionSettings.setMigrationPassword(localdevDatabaseSettings.getAuroraPassword());

    // Common
    dbConnectionSettings.setDatabase(localdevDatabaseSettings.getAuroraDatabase());
    dbConnectionSettings.setSchema(localdevDatabaseSettings.getAuroraSchema());

    return dbConnectionSettings;
  }
}
