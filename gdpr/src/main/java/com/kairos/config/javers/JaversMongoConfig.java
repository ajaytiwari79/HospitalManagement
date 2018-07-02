package com.kairos.config.javers;

import com.google.common.collect.ImmutableMap;
import com.kairos.config.mongoEnv.EnvConfig;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.bson.types.ObjectId;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.MappingStyle;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.json.BasicStringTypeAdapter;
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
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

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
                .registerValueTypeAdapter(new ObjectIdTypeAdapter())
                .registerJaversRepository(javersMongoRepository)
                /* .withListCompareAlgorithm(ListCompareAlgorithm.valueOf(javersProperties.getAlgorithm().toUpperCase()))
                 .withMappingStyle(MappingStyle.valueOf(javersProperties.getMappingStyle().toUpperCase()))
                 .withNewObjectsSnapshot(javersProperties.isNewObjectSnapshot())
                 .withPrettyPrint(javersProperties.isPrettyPrint())
                 .withTypeSafeValues(javersProperties.isTypeSafeValues())
                 .withPackagesToScan(javersProperties.getPackagesToScan())*/
                .build();

    }

    @Bean
    public MongoClient mongo() {
        final List<MongoCredential> credentialList = Arrays.asList(MongoCredential.createCredential(environment.getMongoUserName(), environment.getDataBaseName(), environment.getMongoPassword().toCharArray()));
        return new MongoClient(new ServerAddress(environment.getMongoHost(), environment.getMongoPort()), credentialList);

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


class ObjectIdTypeAdapter extends BasicStringTypeAdapter {

    @Override
    public String serialize(Object sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public Object deserialize(String serializedValue) {
        return new ObjectId(serializedValue);
    }

    @Override
    public Class getValueType() {
        return ObjectId.class;
    }
}

