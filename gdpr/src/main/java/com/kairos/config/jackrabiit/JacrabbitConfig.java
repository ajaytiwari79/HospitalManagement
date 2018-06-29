package com.kairos.config.jackrabiit;


import com.kairos.config.mongoEnv.EnvConfig;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class JacrabbitConfig {


@Inject
private EnvConfig environment;


    @Bean
    public Repository repository() throws RepositoryException {
       final List<MongoCredential> credentialList = Arrays.asList(MongoCredential.createCredential(environment.getMongoUserName(),environment.getDataBaseName(),environment.getMongoPassword().toCharArray()));
        DB db = new MongoClient( new ServerAddress(environment.getMongoHost(),environment.getMongoPort()) ,credentialList).getDB(environment.getDataBaseName());
        DocumentNodeStore ns = new DocumentMK.Builder()
                /*.setBlobStore((BlobStore)new FileBlobStore("mongorepository_jackrabit/blob"))*/.setMongoDB(db,1).getNodeStore();
        return new Jcr(new Oak(ns)).createRepository();

    }


    public JacrabbitConfig()
    {}

}
