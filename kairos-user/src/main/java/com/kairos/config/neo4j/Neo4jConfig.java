package com.kairos.config.neo4j;


import com.kairos.config.env.EnvConfig;
import org.neo4j.ogm.authentication.UsernamePasswordCredentials;
import org.neo4j.ogm.config.ClasspathConfigurationSource;
import org.neo4j.ogm.config.ConfigurationSource;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;

import javax.inject.Inject;

import static com.kairos.constants.Neo4jConstant.*;


/**
 * This is main configuration class for neo4j having two methodsN
 * 1. getSession()
 * 2. getConfiguration()
 */
@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class Neo4jConfig implements EnvironmentAware {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Inject
    private Environment environment;

    @Bean
    public SessionFactory getSessionFactory() {
        return  new SessionFactory(configuration(),"com.kairos.persistence.model");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(getSessionFactory());
    }
    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        ConfigurationSource properties = new ClasspathConfigurationSource("ogm.properties");
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder()
                .
        configuration.getDriverClassName(this.environment.getProperty(NEO4J_DRIVER);
        configuration.
        return configuration;
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
        config
                .driverConfiguration()
                .setDriverClassName(this.environment.getProperty(NEO4J_DRIVER))
                .setURI(this.environment.getProperty(NEO4J_URI)).setCredentials(this.environment.getProperty(NEO4J_USER_NAME),this.environment.getProperty(NEO4J_PASSWORD))
                .setConnectionPoolSize(Integer.parseInt(this.environment.getProperty(CONNECTION_POOL_SIZE)));
        return config;
    }




    /**
     * Bean for enviroment variables
     * @return
     */
    @Bean
    public EnvConfig envConfig(){
        return new EnvConfig();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}