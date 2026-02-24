package com.backend.core.datasources;

import static com.backend.core.datasources.DataSourceType.READER;
import static com.backend.core.datasources.DataSourceType.WRITER;

import java.util.Map;

import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

public class ReadWriteReplicaRoutingConnectionFactory extends AbstractRoutingConnectionFactory {

  public ReadWriteReplicaRoutingConnectionFactory(
      ConnectionFactory writerConnectionFactory, ConnectionFactory readerConnectionFactory) {
    setTargetConnectionFactories(
        Map.of(WRITER, writerConnectionFactory, READER, readerConnectionFactory));
    setDefaultTargetConnectionFactory(writerConnectionFactory);
  }

  @Override
  protected Mono<Object> determineCurrentLookupKey() {
    return TransactionSynchronizationManager.forCurrentTransaction()
        .map(
            synchronizationManager ->
                (Object) (synchronizationManager.isCurrentTransactionReadOnly() ? READER : WRITER))
        .defaultIfEmpty(WRITER);
  }
}
