package com.kairos.config.javers;


import com.kairos.config.env.EnvConfig;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.javers.core.Javers;
import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdatajpa.JaversSpringDataJpaAuditableRepositoryAspect;
import org.javers.spring.boot.sql.JaversSqlProperties;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.javers.spring.jpa.TransactionalJaversBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;

import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

@Configuration
@EnableAspectJAutoProxy
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(value = {JaversSqlProperties.class, JpaProperties.class})
public class JaversConfig {

    private static final Logger logger = LoggerFactory.getLogger(JaversConfig.class);

    @Inject
    private EnvConfig environment;

    private final DialectMapper dialectMapper = new DialectMapper();

    @Autowired
    private JaversSqlProperties javersSqlProperties;

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Bean
    public DialectName javersSqlDialectName() {
        SessionFactoryImplementor sessionFactory =
                (SessionFactoryImplementor) entityManagerFactory.unwrap(SessionFactory.class);

        Dialect hibernateDialect = sessionFactory.getDialect();
        logger.info("detected Hibernate dialect: " + hibernateDialect.getClass().getSimpleName());

        return dialectMapper.map(hibernateDialect);
    }

    @Bean(name = "JaversFromStarter")
    @ConditionalOnMissingBean
    public Javers javers(JaversSqlRepository sqlRepository, PlatformTransactionManager transactionManager) {
        return TransactionalJaversBuilder
                .javers()
                .withTxManager(transactionManager)
                .registerJaversRepository(sqlRepository)
                .withObjectAccessHook(new HibernateUnproxyObjectAccessHook())
                .withProperties(javersSqlProperties)
                .build();
    }

    @Bean(name = "JpaHibernateConnectionProvider")
    @ConditionalOnMissingBean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
    }

   /* @Bean
    public MongoClient mongo() {

        BSON.addEncodingHook(BigInteger.class, new BigIntegerTransformer());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromProviders(new BigIntegerCodecProvider()),
                MongoClient.getDefaultCodecRegistry()
        );
        MongoClientOptions.Builder builder = MongoClientOptions.builder()
                .codecRegistry(codecRegistry);
        final List<MongoCredential> credentialList = Arrays.asList(MongoCredential.createCredential(environment.getDbUserName(), environment.getDataBaseName(), environment.getDbPassword().toCharArray()));
        return new MongoClient(new ServerAddress(environment.getDbHost(), environment.getDbPort()), credentialList, builder.build());

    }
*/
    @Bean
    @ConditionalOnProperty(name = "javers.auditableAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversAuditableAspect javersAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }
    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversSpringDataJpaAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversSpringDataJpaAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    public AuthorProvider authorProvider() {
        return new SpringSecurityAuthorProviderConfig();
    }

    @Bean(name = "EmptyPropertiesProvider")
    @ConditionalOnMissingBean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new EmptyPropertiesProvider();
    }


}