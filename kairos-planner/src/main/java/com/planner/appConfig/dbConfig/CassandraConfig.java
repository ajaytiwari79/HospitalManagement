package com.planner.appConfig.dbConfig;



import com.planner.appConfig.appConfig.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.convert.CassandraConverter;
import org.springframework.data.cassandra.convert.CustomConversions;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;


@PropertySource(value = { "classpath:cassandra.properties" })
@Configuration
@EnableCassandraRepositories(basePackages = { "com.planner.repository" })
public class CassandraConfig extends AbstractCassandraConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(CassandraConfig.class);

    @Autowired
    private AppConfig appConfig;

    @Bean
    public CassandraClusterFactoryBean cluster() {

        CassandraClusterFactoryBean cluster = new CassandraClusterFactoryBean();
        cluster.setContactPoints(appConfig.getCassandraContactPoint());

        return cluster;
    }


    @Bean
    @Override
    public CassandraMappingContext cassandraMapping() throws ClassNotFoundException {

        BasicCassandraMappingContext mappingContext = new BasicCassandraMappingContext();

        mappingContext.setBeanClassLoader(beanClassLoader);
        mappingContext.setInitialEntitySet(CassandraEntityClassScanner.scan(getEntityBasePackages()));

        CustomConversions customConversions = customConversions();

        mappingContext.setCustomConversions(customConversions);
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(), getKeyspaceName()));

        return mappingContext;
    }

    @Bean
    @Override
    public CassandraConverter cassandraConverter() throws ClassNotFoundException {

        MappingCassandraConverter mappingCassandraConverter = new MappingCassandraConverter(cassandraMapping());

        mappingCassandraConverter.setCustomConversions(customConversions());

        return mappingCassandraConverter;
    }


    @Bean
    @Override
    public CassandraSessionFactoryBean session() throws ClassNotFoundException {

        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(cluster().getObject());
        session.setConverter(cassandraConverter());
        session.setKeyspaceName(getKeyspaceName());
        session.setSchemaAction(getSchemaAction());
        session.setStartupScripts(getStartupScripts());
        session.setShutdownScripts(getShutdownScripts());

        return session;
    }

    @Bean
    @Override
    public CassandraAdminOperations cassandraTemplate() throws Exception {

        return new CassandraAdminTemplate(session().getObject(), cassandraConverter());
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    protected String getKeyspaceName() {
        return appConfig.getCassandraKeySpace();
    }
    @Override
    public String[] getEntityBasePackages() {
        return new String[] {"com.planner.domain"}; //com.example package contains the bean with @table annotation
    }
}
