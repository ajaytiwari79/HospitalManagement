package com.kairos.config.jackrabiit_config;


import com.kairos.config.mongoEnv_config.EnvironmentConfig;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

@Configuration
public class JacrabbitConfig {


@Autowired
private EnvironmentConfig environmentConfig;

    @Bean
    public Repository repository() throws RepositoryException {
        DB db = new MongoClient(environmentConfig.getMongoHost(),environmentConfig.getMongoPort()).getDB(environmentConfig.getDataBaseName());
        DocumentNodeStore ns = new DocumentMK.Builder()
                /*.setBlobStore((BlobStore)new FileBlobStore("mongorepository_jackrabit/blob"))*/.setMongoDB(db).getNodeStore();
        Repository repo = new Jcr(new Oak(ns)).createRepository();
        return repo;
    }

}
