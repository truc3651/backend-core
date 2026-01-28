package com.backend.core.datasources;

import static com.backend.core.datasources.DataSourceType.READER;
import static com.backend.core.datasources.DataSourceType.WRITER;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ReadWriteReplicaRoutingDataSource extends AbstractRoutingDataSource {
  @Override
  protected Object determineCurrentLookupKey() {
    return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? READER : WRITER;
  }
}
