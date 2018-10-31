package com.planner.appConfig.dbConfig;


import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

import javax.inject.Inject;

import static com.planner.constants.AppConstants.*;

@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class Neo4jConfig {
    private final static Logger logger = LoggerFactory.getLogger(Neo4jConfig.class);

    @Inject
     Environment environment;

    /**
     * Note here method name must be{sessionFactory}  else
     * spring will not find it.
     * @return
     */
    @Bean
    public SessionFactory sessionFactory() {
        // with domain entity base package(s)
        return new SessionFactory(getConfiguration(),"com.planner.domain");
    }


    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
                .connectionPoolSize(Integer.parseInt(this.environment.getProperty(CONNECTION_POOL_SIZE)))
                .uri(this.environment.getProperty(NEO4J_URI))
                .credentials(this.environment.getProperty(NEO4J_USER_NAME),this.environment.getProperty(NEO4J_PASSWORD))
                .build();
        return configuration;
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }
}
