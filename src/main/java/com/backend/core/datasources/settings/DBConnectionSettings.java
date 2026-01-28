package com.backend.core.datasources.settings;

import lombok.Data;

@Data
public class DBConnectionSettings {
  // Writer
  private String writerHost;
  private String writerPort;
  private String writerUsername;
  private String writerPassword;

  // Reader
  private String readerHost;
  private String readerPort;
  private String readerUsername;
  private String readerPassword;

  // Migration
  private String migrationUsername;
  private String migrationPassword;

  // Common
  private String database;
  private String schema;
}
