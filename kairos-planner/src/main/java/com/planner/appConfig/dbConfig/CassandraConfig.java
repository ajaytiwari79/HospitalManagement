package com.planner.appConfig.dbConfig;



import com.planner.appConfig.appConfig.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.cassandra.config.*;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraAdminTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.convert.CustomConversions;


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
    public CassandraMappingContext cassandraMapping() {

        /*BasicCassandraMappingContext mappingContext = new BasicCassandraMappingContext();

        mappingContext.setBeanClassLoader(getb);
        mappingContext.setInitialEntitySet(CassandraEntityClassScanner.scan(getEntityBasePackages()));*/

        CustomConversions customConversions = customConversions();
        CassandraMappingContext mappingContext= cassandraMapping();

        mappingContext.setCustomConversions(customConversions);
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(), getKeyspaceName()));

        return mappingContext;
    }

    @Bean
    @Override
    public CassandraConverter cassandraConverter() {

        MappingCassandraConverter mappingCassandraConverter = new MappingCassandraConverter(cassandraMapping());

        mappingCassandraConverter.setCustomConversions(customConversions());

        return mappingCassandraConverter;
    }


    @Bean
    @Override
    public CassandraSessionFactoryBean session() {

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
