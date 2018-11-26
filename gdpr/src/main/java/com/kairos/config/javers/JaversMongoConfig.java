package com.kairos.config.javers;


import com.google.common.collect.ImmutableMap;
import com.kairos.config.env.EnvConfig;
import com.kairos.config.codec.BigIntegerCodecProvider;
import com.kairos.config.codec.BigIntegerTransformer;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import com.kairos.utils.user_context.UserContext;
import com.mongodb.*;
import org.bson.BSON;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.commit.CommitMetadata;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    }


}