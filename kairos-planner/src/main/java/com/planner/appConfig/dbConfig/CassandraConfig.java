package com.planner.appConfig.dbConfig;





//@PropertySource(value = { "classpath:cassandra.properties" })
//@Configuration
//@EnableCassandraRepositories(basePackages = { "com.planner.repository" })
public class CassandraConfig{//} extends AbstractCassandraConfiguration {
   /* private static final Logger LOG = LoggerFactory.getLogger(CassandraConfig.class);

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

        *//*BasicCassandraMappingContext mappingContext = new BasicCassandraMappingContext();

        mappingContext.setBeanClassLoader(getb);
        mappingContext.setInitialEntitySet(CassandraEntityClassScanner.scan(getEntityBasePackages()));*//*

        CustomConversions customConversions = customConversions();
        CassandraMappingContext mappingContext= super.cassandraMapping();

        mappingContext.setCustomConversions(customConversions);
        mappingContext.setSimpleTypeHolder(customConversions.getSimpleTypeHolder());
        mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(), getKeyspaceName()));

        return mappingContext;
    }

    @Bean
    @Override
    public CassandraConverter cassandraConverter() {

        MappingCassandraConverter mappingCassandraConverter = null;
        try {
            mappingCassandraConverter = new MappingCassandraConverter(cassandraMapping());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

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
        return new String[] {"com.planner.domain"}; //com.example package contains the bean with //@Table annotation
    }*/
}
