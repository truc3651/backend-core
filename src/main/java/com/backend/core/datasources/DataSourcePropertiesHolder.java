package com.backend.core.datasources;

import java.text.MessageFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourcePropertiesHolder {
  private static final String R2DBC_URL_TEMPLATE = "r2dbc:postgresql://{0}:{1}/{2}?schema={3}";
  private static final String JDBC_URL_TEMPLATE =
      "jdbc:postgresql://{0}:{1}/{2}?currentSchema={3}&stringtype=unspecified&reWriteBatchedInserts=true";

  private String dataSourceType;
  private String host;
  private String port;
  private String username;
  private String password;
  private String database;
  private String schema;

  public String getR2dbcUrl() {
    return MessageFormat.format(R2DBC_URL_TEMPLATE, host, port, database, schema);
  }

  public String getJdbcUrl() {
    return MessageFormat.format(JDBC_URL_TEMPLATE, host, port, database, schema);
  }
}
