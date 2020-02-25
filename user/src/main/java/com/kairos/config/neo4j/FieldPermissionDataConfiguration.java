package com.kairos.config.neo4j;

import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.inject.Inject;


/**
 * This is main configuration class for neo4j having two methodsN
 * 1. getSession()
 * 2. getConfiguration()
 */
/*
@Configuration
@PropertySource({"classpath:application-${spring.profiles.active}.properties"})
@ComponentScan("com.kairos.persistence")
@EnableTransactionManagement
@EnableNeo4jAuditing
*/
public class FieldPermissionDataConfiguration {

    private final Logger logger = LoggerFactory.getLogger(FieldPermissionDataConfiguration.class);


    @Inject
    private Environment environment;

    @Bean(name = "PermissionSessionFactory")
    public SessionFactory getSessionFactory() {
        return new SessionFactory(configuration(), "com.kairos.persistence.model");
    }

   /* @Bean(name = "PermissionNeo4jTransactionManager")
    public Neo4jTransactionManager transactionManager(){
        return new Neo4jTransactionManager(getSessionFactory());
    }*/

    @Bean(name = "PermissionConfiguration")
    public org.neo4j.ogm.config.Configuration configuration() {
        return new org.neo4j.ogm.config.Configuration.Builder()
                .connectionPoolSize(150)
                .uri("bolt://neo4j:oodles@localhost:7687")
               .credentials("neo4j", "oodles")
                .verifyConnection(true)
                .build();
    }

}