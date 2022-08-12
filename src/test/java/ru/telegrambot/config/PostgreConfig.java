package ru.telegrambot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.sql.DataSource;

@Slf4j
@TestConfiguration
public class PostgreConfig {

    /**
     * Запуск и остановка контейнера
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public JdbcDatabaseContainer<?> jdbcDatabaseContainer() {
        return new PostgreSQLContainer<>("postgres:11")
                .waitingFor(Wait.forListeningPort());
    }

    @Bean
    public DataSource dataSource(JdbcDatabaseContainer<?> jdbcDatabaseContainer) {
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcDatabaseContainer.getJdbcUrl());
        hikariConfig.setUsername(jdbcDatabaseContainer.getUsername());
        hikariConfig.setPassword(jdbcDatabaseContainer.getPassword());

        return new HikariDataSource(hikariConfig);
    }

}