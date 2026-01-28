package com.backend.core.datasources;

import java.util.Collection;
import java.util.List;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class DataSourceType {
  public static final String READER = "readerDataSource";
  public static final String WRITER = "writerDataSource";
  public static final String MIGRATION = "migrationDataSource";

  public static final Collection<String> HEALTH_CONTRIBUTING_DATA_SOURCES = List.of(READER, WRITER);
}
