package com.backend.core.datasources;

import static com.backend.core.datasources.DataSourceType.MIGRATION;
import static com.backend.core.datasources.DataSourceType.READER;
import static com.backend.core.datasources.DataSourceType.WRITER;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
//@Configuration
//@ConditionalOnClass({DataSource.class})
//@EnableTransactionManagement
public class DefaultDataSourceConfig {
//  @Autowired protected DataSourceFactory dataSourceFactory;
//
//  @Bean(WRITER)
//  @ConfigurationProperties(prefix = "spring.writer-data-source.hikari")
//  protected HikariDataSource writerDataSource() {
//    return dataSourceFactory.getDataSource(WRITER, dataSourceProperties());
//  }
//
//  @Bean(READER)
//  @ConfigurationProperties(prefix = "spring.reader-data-source.hikari")
//  protected HikariDataSource readerDataSource() {
//    return dataSourceFactory.getDataSource(READER, dataSourceProperties());
//  }
//
//  @Bean(MIGRATION)
//  @FlywayDataSource
//  @ConfigurationProperties(prefix = "spring.migration-data-source.hikari")
//  protected HikariDataSource migrationDataSource() {
//    return dataSourceFactory.getDataSource(MIGRATION, dataSourceProperties());
//  }
//
//  @Bean
//  protected DataSource routingDataSource() {
//    final Map<Object, Object> dataSourceMap = new HashMap<>();
//    dataSourceMap.put(WRITER, writerDataSource());
//    dataSourceMap.put(READER, readerDataSource());
//
//    ReadWriteReplicaRoutingDataSource routingDataSource = new ReadWriteReplicaRoutingDataSource();
//    routingDataSource.setTargetDataSources(dataSourceMap);
//    routingDataSource.setDefaultTargetDataSource(writerDataSource());
//
//    return routingDataSource;
//  }
//
//  @Bean
//  @Primary
//  @DependsOn({"writerDataSource", "readerDataSource", "routingDataSource"})
//  protected DataSource dataSource() {
//    return new LazyConnectionDataSourceProxy(routingDataSource());
//  }
//
//  @Bean
//  @Primary
//  @DependsOn({"dataSource"})
//  protected PlatformTransactionManager transactionManager() {
//    return new DataSourceTransactionManager(dataSource());
//  }
//
//  @Bean
//  @Primary
//  @ConfigurationProperties("spring.datasource")
//  protected DataSourceProperties dataSourceProperties() {
//    return new DataSourceProperties();
//  }
}
