package com.backend.core.datasources;

import static com.backend.core.datasources.DataSourceType.MIGRATION;
import static com.backend.core.datasources.DataSourceType.READER;
import static com.backend.core.datasources.DataSourceType.WRITER;
import static java.lang.String.format;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.backend.core.datasources.settings.DBConnectionSettings;
import com.backend.core.datasources.settings.DBConnectionSettingsProvider;
import com.backend.core.exceptions.ConfigurationException;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConnectionFactoryFactory {
  private final DBConnectionSettingsProvider dbConnectionSettingsProvider;

  public ConnectionFactory getConnectionFactory(String dataSourceType) {
    DBConnectionSettings settings = dbConnectionSettingsProvider.provide();
    DataSourcePropertiesHolder propertiesHolder =
        initializeDataSourcePropertiesHolder(settings, dataSourceType);

    if (!isValidDataSourcePropertiesHolder(propertiesHolder)) {
      throw new ConfigurationException(
          format("Failed to initialize required ConnectionFactory [%s]", dataSourceType));
    }

    log.info("ConnectionFactory [{}] Host: {}", dataSourceType, propertiesHolder.getHost());
    log.info("ConnectionFactory [{}] Database: {}", dataSourceType, propertiesHolder.getDatabase());
    log.info("ConnectionFactory [{}] Username: {}", dataSourceType, propertiesHolder.getUsername());

    ConnectionFactoryOptions options =
        ConnectionFactoryOptions.builder()
            .option(ConnectionFactoryOptions.DRIVER, "postgresql")
            .option(ConnectionFactoryOptions.HOST, propertiesHolder.getHost())
            .option(ConnectionFactoryOptions.PORT, Integer.parseInt(propertiesHolder.getPort()))
            .option(ConnectionFactoryOptions.DATABASE, propertiesHolder.getDatabase())
            .option(ConnectionFactoryOptions.USER, propertiesHolder.getUsername())
            .option(ConnectionFactoryOptions.PASSWORD, propertiesHolder.getPassword())
            .build();

    return ConnectionFactories.get(options);
  }

  private DataSourcePropertiesHolder initializeDataSourcePropertiesHolder(
      DBConnectionSettings dbConnectionSettings, String dataSourceType) {

    DataSourcePropertiesHolder.DataSourcePropertiesHolderBuilder builder =
        DataSourcePropertiesHolder.builder()
            .dataSourceType(dataSourceType)
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
          format("Unsupported ConnectionFactory type supplied [%s]", dataSourceType));
    };
  }

  private boolean isValidDataSourcePropertiesHolder(
      DataSourcePropertiesHolder dataSourcePropertiesHolder) {
    String dataSourceType = dataSourcePropertiesHolder.getDataSourceType();

    if (StringUtils.isBlank(dataSourcePropertiesHolder.getHost())) {
      log.error("ConnectionFactory[{}] connection[host] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getPort())) {
      log.error("ConnectionFactory[{}] connection[port] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getUsername())) {
      log.error("ConnectionFactory[{}] connection[user] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getPassword())) {
      log.error("ConnectionFactory[{}] connection[password] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getDatabase())) {
      log.error("ConnectionFactory[{}] connection[database] is not defined", dataSourceType);
      return false;
    }
    if (StringUtils.isBlank(dataSourcePropertiesHolder.getSchema())) {
      log.error("ConnectionFactory[{}] connection[schema] is not defined", dataSourceType);
      return false;
    }
    return true;
  }
}
