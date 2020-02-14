package com.kairos.config.neo4j;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.SQLException;

@PropertySource({"classpath:application-${spring.profiles.active}.properties"})
@Configuration
public class Neo4jDatasourceConfiguration {

    @Inject
    private Environment environment;

    @Bean
    public DataSource dataSource() throws SQLException {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        config.setUsername(environment.getProperty("spring.datasource.username"));
        config.setPassword(environment.getProperty("spring.datasource.password"));
        return new HikariDataSource(config);
    }
}
