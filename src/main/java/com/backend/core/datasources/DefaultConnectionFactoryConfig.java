package com.backend.core.datasources;

import static com.backend.core.datasources.DataSourceType.READER;
import static com.backend.core.datasources.DataSourceType.WRITER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.reactive.TransactionalOperator;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass({ConnectionFactory.class})
@EnableTransactionManagement
@EnableR2dbcAuditing
public class DefaultConnectionFactoryConfig {
  @Autowired protected ConnectionFactoryFactory connectionFactoryFactory;

  @Bean(WRITER)
  protected ConnectionFactory writerConnectionFactory() {
    return connectionFactoryFactory.getConnectionFactory(WRITER);
  }

  @Bean(READER)
  protected ConnectionFactory readerConnectionFactory() {
    return connectionFactoryFactory.getConnectionFactory(READER);
  }

  @Bean
  protected ConnectionFactory routingConnectionFactory() {
    return new ReadWriteReplicaRoutingConnectionFactory(
        writerConnectionFactory(), readerConnectionFactory());
  }

  @Bean
  @Primary
  @DependsOn({"writerConnectionFactory", "readerConnectionFactory", "routingConnectionFactory"})
  protected ConnectionFactory connectionFactory() {
    return routingConnectionFactory();
  }

  @Bean
  @Primary
  @DependsOn({"connectionFactory"})
  protected ReactiveTransactionManager transactionManager() {
    return new R2dbcTransactionManager(connectionFactory());
  }

  @Bean
  protected TransactionalOperator transactionalOperator(
      ReactiveTransactionManager transactionManager) {
    return TransactionalOperator.create(transactionManager);
  }
}
