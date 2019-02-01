package com.kairos.config.javers;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@EnableAspectJAutoProxy
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JaversMongoConfig {
//TODO

    /*@Inject
    private EnvConfig environment;




    @Bean
    public Javers javers() {
        MongoRepository javersMongoRepository =
                new MongoRepository(mongo().getDatabase(environment.getDataBaseName()));
        return JaversBuilder.javers()
                .registerValue(ManagingOrganization.class)
                .registerValue(Staff.class)
                .registerJaversRepository(javersMongoRepository)
                .build();
    }

    @Bean
    public MongoClient mongo() {

        BSON.addEncodingHook(BigInteger.class, new BigIntegerTransformer());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromProviders(new BigIntegerCodecProvider()),
                MongoClient.getDefaultCodecRegistry()
        );
        MongoClientOptions.Builder builder = MongoClientOptions.builder()
                .codecRegistry(codecRegistry);
        final List<MongoCredential> credentialList = Arrays.asList(MongoCredential.createCredential(environment.getMongoUserName(), environment.getDataBaseName(), environment.getMongoPassword().toCharArray()));
        return new MongoClient(new ServerAddress(environment.getMongoHost(), environment.getMongoPort()), credentialList, builder.build());

    }

    @Bean
    public JaversAuditableAspect javersAuditableAspect() {
        return new JaversAuditableAspect(javers(), authorProvider(), commitPropertiesProvider());
    }

    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        return new JaversSpringDataAuditableRepositoryAspect(javers(), authorProvider(),
                commitPropertiesProvider());
    }

    @Bean
    public AuthorProvider authorProvider() {
        return new SpringSecurityAuthorProviderConfig();
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return () -> ImmutableMap.of("key","ok");

    }*/


}