package com.kairos.config.neo4j;


import com.kairos.config.env.EnvConfig;


import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.annotation.EnableNeo4jAuditing;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.inject.Inject;

import static com.kairos.constants.Neo4jConstant.*;


/**
 * This is main configuration class for neo4j having two methodsN
 * 1. getSession()
 * 2. getConfiguration()
 */
@Configuration

@PropertySource({"classpath:application-${spring.profiles.active}.properties"})
@ComponentScan("com.kairos.persistence")
@EnableTransactionManagement
@EnableNeo4jAuditing
public class Neo4jConfig  implements EnvironmentAware {

    private final Logger logger = LoggerFactory.getLogger(Neo4jConfig.class);


    @Inject
    private Environment environment;

    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(configuration(), "com.kairos.persistence.model");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() throws Exception {
        return new Neo4jTransactionManager(sessionFactory());
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
                .connectionPoolSize(Integer.parseInt(this.environment.getProperty(CONNECTION_POOL_SIZE)))
                .uri(this.environment.getProperty(NEO4J_URI))
               .credentials(this.environment.getProperty(NEO4J_USER_NAME), this.environment.getProperty(NEO4J_PASSCODE))
                .verifyConnection(true)
                .build();

        return configuration;
    }

    /**
     * Bean for environment variables
     *
     * @return
     */
    @Bean
    public EnvConfig envConfig() {
        return new EnvConfig();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}