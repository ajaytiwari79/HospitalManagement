package com.kairos.config.jackrabiit_config;


import com.kairos.config.mongoEnv_config.EnvConfig;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.List;

@Configuration
public class JacrabbitConfig {


@Autowired
private EnvConfig environmentConfig;


private final String username=environmentConfig.getMongoUserName();
private final char[] password=environmentConfig.getMongoPassword().toCharArray();
private final String databaseName=environmentConfig.getDataBaseName();
    final String host = environmentConfig.getMongoHost();
    final int port = environmentConfig.getMongoPort();




    @Bean
    public Repository repository() throws RepositoryException {
        final List<MongoCredential> credentialList = Arrays.asList(MongoCredential.createPlainCredential(username,databaseName,password));

        DB db = new MongoClient( new ServerAddress(host,port),credentialList ).getDB(environmentConfig.getDataBaseName());
        DocumentNodeStore ns = new DocumentMK.Builder()
                /*.setBlobStore((BlobStore)new FileBlobStore("mongorepository_jackrabit/blob"))*/.setMongoDB(db).getNodeStore();
        Repository repo = new Jcr(new Oak(ns)).createRepository();
        return repo;
    }

}
