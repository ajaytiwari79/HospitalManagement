package com.kairos;



import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.Oak;

import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.spi.blob.BlobStore;
import org.apache.jackrabbit.oak.spi.blob.FileBlobStore;
import org.apache.jackrabbit.oak.spi.security.OpenSecurityProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class KairosGdprApplication {

    public static void main(String args[]) {
        SpringApplication.run(KairosGdprApplication.class,args);

    }



    @Bean
    public Repository repository() throws RepositoryException {
   DB db = new MongoClient("127.0.0.1", 27017).getDB("gdpr");
        DocumentNodeStore ns = new DocumentMK.Builder()/*.setBlobStore((BlobStore)new FileBlobStore("mongorepository_jackrabit/blob"))*/.setMongoDB(db).getNodeStore();
        Repository repo = new Jcr(new Oak(ns)).createRepository();
return repo;
    }



}
