package com.backend.core.datasources;

import java.text.MessageFormat;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourcePropertiesHolder {
  private static final String DATA_SOURCE_DRIVER = "org.postgresql.Driver";
  private static final String DATA_SOURCE_URL_TEMPLATE =
      "jdbc:postgresql://{0}:{1}/{2}?currentSchema={3}&stringtype=unspecified&reWriteBatchedInserts=true";

  private String dataSourceType;
  //
  private DataSourceProperties dataSourceCommonProperties;
  private String host;
  private String port;
  private String username;
  private String password;
  private String database;
  private String schema;

  public DataSourceProperties toDataSourceProperties() {
    DataSourceProperties dataSourceProperties = new DataSourceProperties();
    dataSourceCommonProperties =
        Optional.ofNullable(dataSourceCommonProperties).orElseGet(DataSourceProperties::new);

    BeanUtils.copyProperties(dataSourceProperties, dataSourceCommonProperties);
    dataSourceProperties.setUrl(
        MessageFormat.format(DATA_SOURCE_URL_TEMPLATE, host, port, database, schema));
    dataSourceProperties.setDriverClassName(DATA_SOURCE_DRIVER);
    dataSourceProperties.setUsername(username);
    dataSourceProperties.setPassword(password);

    return dataSourceProperties;
  }
}
