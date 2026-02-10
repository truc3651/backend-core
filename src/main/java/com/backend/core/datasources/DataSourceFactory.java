package com.backend.core.datasources;

import static com.backend.core.datasources.DataSourceType.MIGRATION;
import static com.backend.core.datasources.DataSourceType.READER;
import static com.backend.core.datasources.DataSourceType.WRITER;
import static java.lang.String.format;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import com.backend.core.datasources.settings.DBConnectionSettings;
import com.backend.core.datasources.settings.DBConnectionSettingsProvider;
import com.backend.core.exceptions.ConfigurationException;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSourceFactory {
  private final DBConnectionSettingsProvider dbConnectionSettingsProvider;

  public HikariDataSource getDataSource(
      String dataSourceType, DataSourceProperties dataSourceCommonProperties) {
    DBConnectionSettings settings = dbConnectionSettingsProvider.provide();
    DataSourceProperties dataSourceProperties =
        initializeDataSourceProperties(
                settings, dataSourceCommonProperties, dataSourceType);
    log.info("DataSource [{}] URL: {}", dataSourceType, dataSourceProperties.getUrl());
    log.info("DataSource [{}] Username: {}", dataSourceType, dataSourceProperties.getUsername());

    return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  private DataSourceProperties initializeDataSourceProperties(
      DBConnectionSettings dbConnectionSettings,
      DataSourceProperties dataSourceCommonProperties,
      String dataSourceType) {

    DataSourcePropertiesHolder dataSourcePropertiesHolder =
        initializeDataSourcePropertiesHolder(
            dbConnectionSettings, dataSourceCommonProperties, dataSourceType);
    if (!isValidDataSourcePropertiesHolder(dataSourcePropertiesHolder)) {
      throw new ConfigurationException(
          format("Failed to initialize required DataSource [%s]", dataSourceType));
    }
    return dataSourcePropertiesHolder.toDataSourceProperties();
  }

  private DataSourcePropertiesHolder initializeDataSourcePropertiesHolder(
      DBConnectionSettings dbConnectionSettings,
      DataSourceProperties dataSourceCommonProperties,
      String dataSourceType) {

    DataSourcePropertiesHolder.DataSourcePropertiesHolderBuilder builder =
        DataSourcePropertiesHolder.builder()
            .dataSourceType(dataSourceType)
            .dataSourceCommonProperties(dataSourceCommonProperties)
            .database(dbConnectionSettings.getDatabase())
            .schema(dbConnectionSettings.getSchema());

    return switch (dataSourceType) {
      case READER -> builder
          .host(dbConnectionSettings.getReaderHost())
          .port(dbConnectionSettings.getReaderPort())
          .username(dbConnectionSettings.getReaderUsername())
          .password(dbConnectionSettings.getReaderPassword())
          .build();
      case WRITER -> builder
          .host(dbConnectionSettings.getWriterHost())
          .port(dbConnectionSettings.getWriterPort())
          .username(dbConnectionSettings.getWriterUsername())
          .password(dbConnectionSettings.getWriterPassword())
          .build();
      case MIGRATION -> builder
          .host(dbConnectionSettings.getWriterHost())
          .port(dbConnectionSettings.getWriterPort())
          .username(dbConnectionSettings.getMigrationUsername())
          .password(dbConnectionSettings.getMigrationPassword())
          .build();
      default -> throw new ConfigurationException(
          format("Unsupported DataSource type supplied [%s]", dataSourceType));
    };
  }

  private boolean isValidDataSourcePropertiesHolder(
      DataSourcePropertiesHolder dataSourcePropertiesHolder) {
    String dataSourceType = dataSourcePropertiesHolder.getDataSourceType();

    if (StringUtils.isBlank(dataSourcePropertiesHolder.getHost())) {
      log.error("DataSource[{}] connection[host] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getPort())) {
      log.error("DataSource[{}] connection[port] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getUsername())) {
      log.error("DataSource[{}] connection[user] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getPassword())) {
      log.error("DataSource[{}] connection[password] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getDatabase())) {
      log.error("DataSource[{}] connection[database] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getSchema())) {
      log.error("DataSource[{}] connection[schema] is not defined", dataSourceType);
      return false;
    }
    return true;
  }
}
