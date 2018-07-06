package com.kairos.config.javers;


import com.google.common.collect.ImmutableMap;
import com.kairos.config.mongoEnv.EnvConfig;
import com.kairos.utils.codec.BigIntegerCodec;
import com.kairos.utils.codec.BigIntegerCodecProvider;
import com.kairos.utils.codec.BigIntegerTransformer;
import com.mongodb.*;
import org.bson.BSON;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitFactory;
import org.javers.core.diff.DiffFactory;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.PrimitiveType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.metamodel.type.ValueType;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.jql.QueryRunner;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableAspectJAutoProxy
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JaversMongoConfig {


    @Inject
    private EnvConfig environment;



    @Bean
    public Javers javers() {
        MongoRepository javersMongoRepository =
                new MongoRepository(mongo().getDatabase(environment.getDataBaseName()));
        return JaversBuilder.javers()
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
        /*MongoClientOptions settings = MongoClientOptions.builder().readPreference(ReadPreference.nearest())
                .codecRegistry(codecRegistry).build();*/
        final List<MongoCredential> credentialList = Arrays.asList(MongoCredential.createCredential(environment.getMongoUserName(), environment.getDataBaseName(), environment.getMongoPassword().toCharArray()));
        return new MongoClient(new ServerAddress(environment.getMongoHost(), environment.getMongoPort()), credentialList,builder.build());

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
        return new SpringSecurityAuthorProvider();
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return () -> ImmutableMap.of("key", "ok");
    }




}