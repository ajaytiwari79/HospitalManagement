package com.kairos.config.javers;

//@Configuration
//@EnableAspectJAutoProxy
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class JaversMongoConfig {

/*
  //  @Inject
    private EnvConfig environment;


    //@Bean
    public Javers javers() {
        MongoRepository javersMongoRepository =
                new MongoRepository(mongo().getDatabase(environment.getDataBaseName()));
        return JaversBuilder.javers()
                .registerJaversRepository(javersMongoRepository)
                .build();
    }

    //@Bean
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

    //@Bean
    public JaversAuditableAspect javersAuditableAspect() {
        return new JaversAuditableAspect(javers(), authorProvider(), commitPropertiesProvider());
    }

    //@Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        return new JaversSpringDataAuditableRepositoryAspect(javers(), authorProvider(),
                commitPropertiesProvider());
    }

    //@Bean
    public AuthorProvider authorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    //@Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        //String userName = UserContext.getUserDetails().getFirstName() + UserContext.getUserDetails().getLastName();
        return () -> ImmutableMap.of("key", "ok");
    }*/


}